import json
import requests
from app.global_data.global_data import g


def http_client(service_name, body=None, jwt=None):
    headers = {
        'Content-Type': 'application/json;charset=UTF-8',
        'Accept': '*/*',
    }

    if jwt is not None:
        headers['Authorization'] = 'Bearer {}'.format(jwt)

    host = g.consul_client.resolve_service(service_name)
    url = 'http://{}/api/data'.format(host)

    try:
        res = requests.post(url=url, json=body, headers=headers)
    except:
        return {
            'status': 'err',
            'value': 'HTTP访问失败!'
        }

    if res.status_code != 200:
        return {
            'status': 'err',
            'value': res.text
        }

    try:
        # JSON数据，转化成JSON对象
        result = json.loads(res.text, encoding='utf-8')
    except:
        # 非JSON数据（二进制字节流），直接返回
        result = res.content

    return result
