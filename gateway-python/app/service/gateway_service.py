import json
import base64
import requests
from app.service import token_service
from app.global_data.global_data import g


def forward_request(prev_request):
    if not check_allow_forward(prev_request):
        raise Exception('500 server unavailable')

    next_request, new_access_token, new_refresh_token = gen_next_request(prev_request)
    if next_request is None:
        raise Exception('500 server unavailable')

    value = {
        'response': None,
        'new_access_token': None,
        'new_refresh_token': None,
    }
    if new_access_token is not None and new_refresh_token is not None:
        value['new_access_token'] = new_access_token
        value['new_refresh_token'] = new_refresh_token

    value['response'] = requests.request(
        method=next_request['method'],
        url=next_request['url'],
        data=next_request['body'],
        headers=next_request['headers'],
        timeout=600
    )

    return value


def get_service_name(path):
    if path == '/':
        return 'portal'

    if path.rfind('/') == 0:
        service_name = path[1:]
    else:
        path = path[1:]
        if path.startswith('zuul'):
            # 为了兼容采用zuul作为gateway的前端代码
            path = path[5:]
        service_name = path[: path.find('/')]

    if service_name in g.consul_client.get_service_names():
        return service_name

    # 前端或后端微服务未启动（默认后端微服务以'u'打头，前端微服务以'p'打头，特殊的例外），直接返回错误。其他命名情况会默认转发至portal。
    if '.' not in service_name and (service_name.startswith('u') or service_name.startswith('p') or service_name == 'ability'):
        return None

    return 'portal'


def strip_service_name(uri, service_name):
    if uri.find('/zuul') == 0:
        # 为了兼容采用zuul作为gateway的前端代码
        uri = uri[5:]
    uri = uri[len(service_name) + 1:]
    if uri == '':
        uri = '/'

    return uri


def check_allow_forward(prev_request):
    # /uaa/oauth 相关获取公钥和令牌的接口，只限系统内部微服务访问，不允许外部系统通过网关访问
    if prev_request.path.startswith('/uaa/'):
        try:
            body = json.loads(str(prev_request.body, encoding='utf-8'))
            action = body.get('action')
            if action == 'get_public_key' or action == 'get_token':
                return False
        except:
            return True

    return True


def gen_next_request(prev_request):
    service_name = get_service_name(prev_request.path)
    if service_name is None:
        return None, None, None
    
    uri = prev_request.uri
    if service_name != 'portal':
        uri = strip_service_name(uri, service_name)

    host = g.consul_client.resolve_service(service_name)
    if host is None:
        return None, None, None
    url = 'http://{}{}'.format(host, uri)

    try:
        access_token = prev_request.cookies.get('access_token').value
        refresh_token = prev_request.cookies.get('refresh_token').value
    except:
        access_token = refresh_token = None

    method = prev_request.method
    body = prev_request.body if method == 'POST' or method == 'PUT' else None
    headers = prev_request.headers

    next_request = {
        'url': url,
        'method': method,
        'body': body,
        'headers': headers,
    }

    if access_token:  # not None and not ''
        # 转发HTTP请求时，请求头中用Authorization替换cookie
        next_request['headers'].add('Authorization', 'Bearer ' + access_token)
        next_request['headers'].pop('Cookie')

    token = token_service.get_token(next_request)
    new_access_token = new_refresh_token = None  # new token为None时，不需要重置浏览器cookie中token
    # 如果有从浏览器带过来的token，但是已经失效，则尝试使用refresh token申请新token
    if access_token and not token.is_valid:
        url = g.oauth_client.get_uaa_url()
        headers = {
            'Authorization': 'Basic {}'.format(str(base64.b64encode(b'web_app:changeit'), encoding='utf-8'))
        }
        body = {
            'action': 'get_token',
            'args': {
                'grant_type': 'refresh_token',
                'refresh_token': refresh_token,
            }
        }

        res = requests.post(url, json=body, headers=headers)
        if res.status_code == 200:
            res_body = json.loads(res.text)
            if res_body['status'] == 'ok':
                new_access_token = res_body['value'].get('access_token')
                new_refresh_token = res_body['value'].get('refresh_token')
                next_request['headers'].pop('Authorization')
                next_request['headers'].add('Authorization', 'Bearer ' + new_access_token)
            else:
                new_access_token = new_refresh_token = ''  # token失效且无法申请新token，则清除浏览器中cookie中的token
                next_request['headers'].pop('Authorization')
        else:
            new_access_token = new_refresh_token = ''  # token失效且无法申请新token，则清除浏览器中cookie中的token
            next_request['headers'].pop('Authorization')

    return next_request, new_access_token, new_refresh_token
