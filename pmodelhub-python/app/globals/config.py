import os
from app.utils.loacl_ip import get_local_ip


class Config:
    def __init__(self):
        self.app_name = 'pmodelhub'
        self.app_version = '1.0.0'
        self.server_ip = get_local_ip()
        self.server_port = 8202

        self.consul_address = '127.0.0.1'
        self.consul_port = 8500
        self.consul_tags = ['profile-dev']
        self.consul_http_check_url = 'http://{}:{}/management/health'.format(self.server_ip, self.server_port)

        self.cookie_secret = 'change_it'

        self.env = os.environ.get('APP_PROFILE', 'dev').lower()
        if self.env == 'prod':
            self.consul_address = 'consul'
            self.consul_tags = ['profile-prod']
