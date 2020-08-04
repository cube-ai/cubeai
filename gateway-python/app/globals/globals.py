import uuid
from app.globals.config import Config
from app.globals.consul_client import ConsulClient
from app.globals.oauth_client import OauthClient
import logging


class Globals:
    def __init__(self):
        self.inited = False
        self.init_success = False

        self.config = None
        self.consul_client = None
        self.oauth_client = None
        self.k8s_client = None
        self.central_config = None

    def load_globals_data(self):
        self.config = Config()

        log_level = logging.DEBUG if self.config.env == 'dev' else logging.ERROR
        logging.basicConfig(level=log_level, format='%(asctime)s - %(levelname)s - %(message)s')

        self.consul_client = ConsulClient(self.config.consul_address, self.config.consul_port)
        service_id = '{}-{}'.format(self.config.app_name, str(uuid.uuid4()).replace('-', ''))
        self.consul_client.register(self.config.app_name, service_id, self.config.server_ip, self.config.server_port,
                                    self.config.consul_tags, self.config.consul_http_check_url)
        if not self.consul_client.registered:
            self.inited = True
            self.init_success = False
            logging.error('向Consul注册微服务失败')
            return

        self.oauth_client = OauthClient(self.consul_client)
        if self.oauth_client.uaa_public_key is None:
            self.inited = True
            self.init_success = False
            logging.error('初始化OauthClient失败')
            return

        self.inited = True
        self.init_success = True

    def get_central_config(self):
        if self.central_config is None:
            self.central_config = self.consul_client.get_kv()

        return self.central_config


g = Globals()
