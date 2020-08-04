import json
import requests
from app.globals.globals import g
import logging


def http_client(method, service_name, path, body=None, jwt=None):
    headers = {
        'Content-Type': 'application/json;charset=UTF-8',
        'Accept': '*/*',
    }

    if jwt is not None:
        headers['Authorization'] = 'Bearer {}'.format(jwt)

    host = g.consul_client.resolve_service(service_name)
    url = 'http://{}{}'.format(host, path)

    if method == 'get':
        res = requests.get(url=url, headers=headers)
    elif method == 'post':
        res = requests.post(url=url, data=body, headers=headers)
    elif method == 'put':
        res = requests.put(url=url, data=body, headers=headers)
    elif method == 'delete':
        res = requests.delete(url=url, headers=headers)
    else:
        logging.info('No supported HTTP method')
        res = None

    if res and res.status_code == 200:
        try:
            res = json.loads(res.text)
        except:
            res = res.text
    else:
        res = None

    return res
