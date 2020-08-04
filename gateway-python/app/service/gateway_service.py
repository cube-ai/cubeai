import json
import base64
from tornado.httpclient import AsyncHTTPClient, HTTPRequest
from app.service import token_service
from app.globals.globals import g


def get_service_name(path):
    if path == '/':
        return 'portal'

    if path.rfind('/') == 0:
        service_name = path[1:]
    else:
        path = path[1:]
        if path.find('zuul') == 0:
            # 为了兼容采用zuul作为gateway的前端代码
            path = path[5:]
        service_name = path[: path.find('/')]

    if service_name in g.consul_client.get_service_names():
        return service_name

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
    if prev_request.path.find('/uaa/oauth') == 0:
        return False

    return True


async def gen_next_request(prev_request):
    if prev_request.path.find('/uaa/api/attachments') == 0:
        timeout = 600
    else:
        timeout = 120

    service_name = get_service_name(prev_request.path)
    uri = prev_request.uri
    if service_name != 'portal':
        uri = strip_service_name(uri, service_name)

    host = await g.consul_client.async_resolve_service(service_name)
    if host is None:
        return None, None, None
    url = 'http://{}{}'.format(host, uri)

    try:
        access_token = prev_request.cookies.get('access_token').value
        refresh_token = prev_request.cookies.get('session_token').value
    except:
        access_token = refresh_token = None

    method = prev_request.method
    body = prev_request.body if method == 'POST' or method == 'PUT' else None
    headers = prev_request.headers
    try:
        headers.pop('If-Modified-Since')
    except:
        pass
    try:
        headers.pop('If-None-Match')
    except:
        pass
    next_request = HTTPRequest(
        url=url,
        method=method,
        body=body,
        headers=headers,
        request_timeout=timeout
    )
    if access_token:  # not None and not ''
        # 转发HTTP请求时，请求头中用Authorization替换cookie
        next_request.headers.add('Authorization', 'Bearer ' + access_token)
        next_request.headers.pop('Cookie')

    token = token_service.get_token(next_request)
    new_access_token = new_refresh_token = None  # new token为None时，不需要重置浏览器cookie中token
    # 如果有从浏览器带过来的token，但是已经失效，则尝试使用refresh token申请新token
    if access_token and not token.is_valid:
        headers = {
            'Accept': 'application/json, application/*+json',
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': 'Basic {}'.format(str(base64.b64encode(b'web_app:changeit'), encoding='utf-8'))
        }
        url = await g.oauth_client.async_get_jwt_url()
        params = '?grant_type=refresh_token&refresh_token={}'.format(refresh_token)
        request = HTTPRequest(url=url + params,
                              method='POST',
                              body='{}',
                              headers=headers)

        http = AsyncHTTPClient()
        try:
            res = await http.fetch(request)
            if res.code == 200:
                res_body = json.loads(str(res.body, encoding='utf-8'))
                new_access_token = res_body.get('access_token')
                new_refresh_token = res_body.get('refresh_token')
                next_request.headers.pop('Authorization')
                next_request.headers.add('Authorization', 'Bearer ' + new_access_token)
            else:
                new_access_token = new_refresh_token = ''  # token失效且无法申请新token，则清除浏览器中cookie中的token
                next_request.headers.pop('Authorization')
        except Exception as e:
            new_access_token = new_refresh_token = ''  # token失效且无法申请新token，则清除浏览器中cookie中的token
            next_request.headers.pop('Authorization')

    return next_request, new_access_token, new_refresh_token
