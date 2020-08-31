import json
import base64
import requests
from app.global_data.global_data import g


def login(**args):
    username = args.get('username')
    password = args.get('password')
    verify_id = args.get('verifyId')
    verify_code = args.get('verifyCode')

    url = g.oauth_client.get_uaa_url()
    body1 = {
        'action': 'validate_verify_code',
        'args': {
            'verifyId': verify_id,
            'verifyCode': verify_code,
        }
    }
    res = requests.post(url, json=body1)

    if res.status_code != 200:
        raise Exception('server_error')

    verify_result = json.loads(res.text, encoding='utf-8')
    if verify_result['status'] != 'ok' or verify_result['value'] != 1:
        raise Exception('verify_code_error')

    headers2 = {
        'Authorization': 'Basic {}'.format(str(base64.b64encode(b'web_app:changeit'), encoding='utf-8'))
    }
    body2 = {
        'action': 'get_token',
        'args': {
            'grant_type': 'password',
            'username': username,
            'password': password,
        }
    }
    res = requests.post(url, json=body2, headers=headers2)

    if res.status_code != 200:
        raise Exception('server_error')

    res_body = json.loads(res.text, encoding='utf-8')
    if res_body['status'] != 'ok':
        raise Exception('password_error')

    return {
        'access_token': res_body['value'].get('access_token'),
        'refresh_token': res_body['value'].get('refresh_token'),
    }


def logout(**args):
    return {
        'access_token': '',
        'refresh_token': '',
    }
