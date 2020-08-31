import uuid
from app.global_data.config import Config
from app.global_data.consul_client import ConsulClient
from app.global_data.oauth_client import OauthClient
from app.global_data.k8s_client import K8sClient
import logging


class GlobalData:
    def __init__(self):
        self.inited = False
        self.init_success = False

        self.config = None
        self.consul_client = None
        self.oauth_client = None
        self.k8s_client = None
        self.central_config = None

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

        cfg = self.get_central_config()
        self.k8s_client = K8sClient(
            host=cfg['kubernetes']['api']['url'],
            token=cfg['kubernetes']['api']['token'],
            ipIpTunnelCidr=cfg['kubernetes']['api']['ipIpTunnelCidr']
        )

        try:
            self.k8s_client.init_v1api()
        except Exception as e:
            self.inited = True
            self.init_success = False
            logging.error(e)
            logging.error('初始化k8s_client失败')
            return

        self.inited = True
        self.init_success = True

    def get_central_config(self):
        if self.central_config is None:
            self.central_config = self.consul_client.get_kv()

        return self.central_config


g = GlobalData()
