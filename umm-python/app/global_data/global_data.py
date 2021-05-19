import uuid
import json
import time
import threading
import websocket
from app.global_data.config import Config
from app.global_data.consul_client import ConsulClient
from app.global_data.oauth_client import OauthClient
from app.global_data.db_client import DataBaseClient
import logging


class GlobalData:
    def __init__(self):
        self.inited = False
        self.init_success = False

        self.config = None
        self.central_config = None
        self.consul_client = None
        self.oauth_client = None
        self.db = None
        self.websocket = None

    def load_global_data(self):
        self.config = Config()

        self.consul_client = ConsulClient(self.config.consul_address, self.config.consul_port)
        service_id = '{}-{}'.format(self.config.app_name, str(uuid.uuid4()).replace('-', ''))
        self.consul_client.register(self.config.app_name, service_id, self.config.server_ip, self.config.server_port,
                                    self.config.consul_tags, self.config.consul_http_check_url)
        if not self.consul_client.registered:
            self.inited = True
            self.init_success = False
            logging.error('向Consul注册微服务失败')
            return

        try:
            self.oauth_client = OauthClient(self.consul_client)
        except:
            self.inited = True
            self.init_success = False
            logging.error('从uaa初始化OauthClient失败！')
            return
        if self.oauth_client.uaa_public_key is None or self.oauth_client.jwt is None:
            self.inited = True
            self.init_success = False
            logging.error('从uaa初始化OauthClient失败！')
            return

        self.db = DataBaseClient(self.config.db_host, self.config.db_port,
                                 self.config.db_username, self.config.db_password, self.config.db_name)
        try:
            self.db.init_db()
        except Exception as e:
            self.inited = True
            self.init_success = False
            logging.error(e)
            logging.error('初始化数据库失败')
            return

        thread = threading.Thread(target=websocket_client_thread)
        thread.setDaemon(True)
        thread.start()

        self.inited = True
        self.init_success = True

    def get_central_config(self):
        if self.central_config is None:
            self.central_config = self.consul_client.get_kv()

        return self.central_config


def websocket_client_thread():

    def on_open(ws):
        ws.send(json.dumps({
            'type': 'subscribe',
            'content': g.config.app_name,
        }))

    while True:
        time.sleep(1)
        while True:
            time.sleep(1)
            gateway = g.consul_client.resolve_service('gateway')
            if gateway is not None:
                g.websocket = websocket.WebSocketApp('ws://{}/websocket'.format(gateway), on_open=on_open)
                logging.critical('Connecting to WebSocket server: {} ...'.format(gateway))
                break

        g.websocket.run_forever()
        logging.error('WebSocket connection broken, try connect again ...')


g = GlobalData()
