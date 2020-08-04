import json
import base64
import requests
from datetime import datetime


class OauthClient:
    def __init__(self, consul_client):
        self.consul_client = consul_client
        self.uaa_name = 'uaa'
        self.uaa_public_key =None
        self.jwt = None
        self.jwt_expire = None
        self.update_public_key()
        self.update_jwt()

    def get_jwt_url(self):
        return 'http://{}/oauth/token'.format(self.consul_client.resolve_service(self.uaa_name))

    def get_public_key_url(self):
        return 'http://{}/oauth/token_key'.format(self.consul_client.resolve_service(self.uaa_name))

    def update_public_key(self):
        res = requests.get(url=self.get_public_key_url())
        self.uaa_public_key = json.loads(res.text).get('value') if res.status_code == 200 else None
        return self.uaa_public_key

    def update_jwt(self):
        body = {
            'grant_type': 'client_credentials',
        }
        headers = {
            'Accept': 'application/json, application/*+json',
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': 'Basic {}'.format(str(base64.b64encode(b'internal:internal'), encoding='utf-8'))
        }

        res = requests.post(url=self.get_jwt_url(), data=body, headers=headers)

        if res.status_code == 200:
            self.jwt = json.loads(res.text).get('access_token')
            self.jwt_expire = get_jwt_expire(self.jwt)  # 通过缓存exp来验证jwt是否有效，节省验证时间
        else:
            self.jwt = None
            self.jwt_expire = None
        return self.jwt

    def get_public_key(self):
        return self.uaa_public_key if self.uaa_public_key is not None else self.update_public_key()

    def get_jwt(self):
        return self.jwt if not verify_expire(self.jwt_expire) else self.update_jwt()


def get_jwt_expire(jwt):
    _, payload, _ = jwt.split('.')
    missing_padding = (4 - len(payload) % 4) % 4
    payload += '=' * missing_padding
    try:
        return json.loads(str(base64.b64decode(payload.encode()), encoding='utf-8')).get('exp')
    except:
        return None


def verify_expire(exp):
    if exp is None:
        return True
    now = int(datetime.now().timestamp())
    return now + 5 > exp


# 暂时不用该函数，用verify_expire代替，以节省验证时间
def verify_jwt_expire(jwt):
    _, payload, _ = jwt.split('.')
    missing_padding = (4 - len(payload) % 4) % 4
    payload += '=' * missing_padding
    try:
        exp = json.loads(str(base64.b64decode(payload.encode()), encoding='utf-8')).get('exp')
        now = int(datetime.now().timestamp())
        return now + 5 > exp
    except:
        return True


if __name__ == "__main__":
    from app.globals.config import Config
    from app.globals.consul_client import ConsulClient

    config = Config()
    consul_client = ConsulClient(config.consul_address, config.consul_port)
    oauth_client = OauthClient(consul_client)

    print(oauth_client.get_public_key())
    print(oauth_client.get_jwt())
