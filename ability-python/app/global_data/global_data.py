import uuid
import json
import time
import threading
import websocket
import requests
from app.global_data.config import Config
from app.global_data.consul_client import ConsulClient
from app.global_data.oauth_client import OauthClient
import logging


class GlobalData:
    def __init__(self):
        self.inited = False
        self.init_success = False

        self.config = None
        self.consul_client = None
        self.oauth_client = None
        self.central_config = None
        self.websocket_gw = None
        self.websocket_models = {}

    def load_global_data(self):
        self.config = Config()

        self.consul_client = ConsulClient(self.config.consul_address, self.config.consul_port)
        service_id = '{}-{}'.format(self.config.app_name, str(uuid.uuid4()).replace('-', ''))
        self.consul_client.register(self.config.app_name, service_id, self.config.server_ip, self.config.server_port,
                                    self.config.consul_tags, self.config.consul_http_check_url)
        if not self.consul_client.registered:
            self.inited = True
            self.init_success = False
            logging.error('向Consul注册微服务失败！')
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

        thread = threading.Thread(target=websocket_client_thread_gw)
        thread.setDaemon(True)
        thread.start()

        self.inited = True
        self.init_success = True

    def get_central_config(self):
        if self.central_config is None:
            self.central_config = self.consul_client.get_kv()

        return self.central_config


def websocket_client_thread_gw():
    def on_open(ws):
        ws.send(json.dumps({
            'type': 'subscribe',
            'content': 'ability',
        }))

    def on_message(ws, message):
        try:
            msg = json.loads(message, encoding='utf-8')
        except Exception as e:
            logging.error(str(e))
            logging.error('WebSocket消息必须采用JSON格式！')
            return

        receiver = msg.get('receiver')
        if receiver is None:
            return

        next_websocket = g.websocket_models.get(receiver)
        if next_websocket:
            next_websocket.send(message)
        else:
            thread = threading.Thread(target=websocket_client_thread_model, args=(receiver, ))
            thread.setDaemon(True)
            thread.start()
            time.sleep(0.1)
            next_websocket = g.websocket_models.get(receiver)
            if next_websocket:
                next_websocket.send(message)

    while True:
        time.sleep(1)
        while True:
            time.sleep(1)
            gateway = g.consul_client.resolve_service('gateway')
            if gateway is not None:
                g.websocket_gw = websocket.WebSocketApp(
                    'ws://{}/websocket'.format(gateway),
                    on_open=on_open,
                    on_message=on_message,
                )
                logging.critical('Connecting to WebSocket server: {} ...'.format(gateway))
                break

        g.websocket_gw.run_forever()
        logging.error('Gateway WebSocket connection broken, try connect again ...')


def websocket_client_thread_model(receiver):
    def on_message(ws, message):
        g.websocket_gw.send(message)

    host = g.consul_client.resolve_service('umm')
    url = 'http://{}/api/data'.format(host)
    res = requests.post(url=url, json={
        'action': 'get_deployments',
        'args': {
            'uuid': receiver,
        },
    })
    result = json.loads(res.text, encoding='utf-8')
    deployment = result['value']['results'][0]
    k8s_port = deployment.get('k8sPort')

    internal_ip = g.get_central_config()['kubernetes']['ability']['internalIP']
    url = 'ws://{}:{}/websocket'.format(internal_ip, k8s_port)

    ws_conn = websocket.WebSocketApp(
        url,
        on_message=on_message,
    )
    g.websocket_models[receiver] = ws_conn
    logging.critical('Connecting to Model WebSocket server: {} ...'.format(receiver))

    ws_conn.run_forever()
    logging.error('Model WebSocket connection broken: {}'.format(receiver))
    g.websocket_models.pop(receiver)


g = GlobalData()
