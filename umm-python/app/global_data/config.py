import os
import yaml
from app.utils.loacl_ip import get_local_ip


class Config:
    def __init__(self):
        with open('./application.yml', 'r') as f:
            yml = yaml.load(f, Loader=yaml.SafeLoader)

        self.app_name = yml['service']['ename']

        try:
            self.app_version = yml['service']['version']
        except:
            self.app_version = '0.0.1'

        self.server_ip = get_local_ip()

        self.app_profile = os.environ.get('APP_PROFILE', 'dev').lower()

        if self.app_profile == 'dev':
            try:
                self.server_port = yml['service']['port']['dev']
            except:
                self.server_port = 80
            self.consul_address = '127.0.0.1'
            self.consul_tags = ['profile-dev']
            self.consul_http_check_url = 'http://{}:{}/management/health'.format(self.server_ip, self.server_port)
        else:
            try:
                self.server_port = yml['service']['port']['prod']
            except:
                self.server_port = 80
            self.consul_address = 'consul'
            self.consul_tags = ['profile-prod']
        self.consul_port = 8500
        self.consul_http_check_url = 'http://{}:{}/management/health'.format(self.server_ip, self.server_port)

        if self.app_profile == 'dev':
            self.db_host = '127.0.0.1'
            self.db_port = 3307
        else:
            self.db_host = 'umm-mysql'
            self.db_port = 3306
        self.db_username = 'root'
        self.db_password = ''
        self.db_name = 'umm'
