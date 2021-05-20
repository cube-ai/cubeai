import json
import base64
import requests
from app.global_data.global_data import g


def third_party_oauth(request):
    third_party = None
    if request.path.startswith('/oauth/qq'):
        third_party = 'qq'
    elif request.path.startswith('/oauth/gitee'):
        third_party = 'gitee'
    elif request.path.startswith('/oauth/github'):
        third_party = 'github'

    if third_party is None:
        raise Exception('非法第三方服务器！')

    code = str(request.arguments.get('code')[0], encoding='utf-8')

    url = g.oauth_client.get_uaa_url()
    headers = {
        'Authorization': 'Basic {}'.format(str(base64.b64encode(b'internal:internal'), encoding='utf-8'))
    }
    body = {
        'action': 'get_token',
        'args': {
            'grant_type': 'third_party_oauth',
            'third_party': third_party,
            'code': code,
        }
    }
    res = requests.post(url, json=body, headers=headers)

    if res.status_code != 200:
        raise Exception('第三方认证失败！')

    res_body = json.loads(res.text, encoding='utf-8')
    if res_body['status'] != 'ok':
        raise Exception('第三方认证失败！')

    return {
        'access_token': res_body['value'].get('access_token'),
        'refresh_token': res_body['value'].get('refresh_token'),
    }
