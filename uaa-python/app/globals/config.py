import os
import rsa
from app.utils.loacl_ip import get_local_ip


class Config:
    def __init__(self):
        self.app_name = 'uaa'
        self.app_version = '1.0.0'
        self.server_ip = get_local_ip()
        self.server_port = 9999

        self.consul_address = '127.0.0.1'
        self.consul_port = 8500
        self.consul_tags = ['profile-dev']
        self.consul_http_check_url = 'http://{}:{}/management/health'.format(self.server_ip, self.server_port)

        self.db_host = '127.0.0.1'
        self.db_port = 3306
        self.db_username = 'root'
        self.db_password = ''
        self.db_name = 'uaa'

        self.cookie_secret = 'change_it'

        if not os.path.exists('app/resources/public_key.pem') or not os.path.exists('app/resources/private_key.pem'):
            public_key, private_key = rsa.newkeys(2048)
            self.public_key = public_key.save_pkcs1()
            self.private_key = private_key.save_pkcs1()
            with open("app/resources/public_key.pem", "wb") as file:
                file.write(self.public_key)
            with open("app/resources/private_key.pem", "wb") as file:
                file.write(self.private_key)
        else:
            with open("app/resources/public_key.pem", "rb") as file:
                self.public_key = file.read()
            with open("app/resources/private_key.pem", "rb") as file:
                self.private_key = file.read()

        self.env = os.environ.get('APP_PROFILE', 'dev').lower()
        if self.env == 'prod':
            self.consul_address = 'consul'
            self.consul_tags = ['profile-prod']

            self.db_host = 'uaa-mysql'
            self.db_port = 3306
