import json
from tornado.httpclient import AsyncHTTPClient, HTTPRequest
from app.globals.globals import g


async def async_http_client(method, service_name, path, body=None, jwt=None):
    headers = {
        'Content-Type': 'application/json;charset=UTF-8',
        'Accept': '*/*',
    }

    if jwt is not None:
        headers['Authorization'] = 'Bearer {}'.format(jwt)

    host = await g.consul_client.async_resolve_service(service_name)
    url = 'http://{}{}'.format(host, path)

    request = HTTPRequest(
        url=url,
        method=method.upper(),
        body=body,
        headers=headers)

    http = AsyncHTTPClient()
    try:
        res = await http.fetch(request)
    except Exception as e:
        return None

    try:
        res = json.loads(str(res.body, encoding='utf-8'))
    except:
        res = str(res.body, encoding='utf-8')

    return res
