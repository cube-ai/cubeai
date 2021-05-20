from tornado.httpclient import AsyncHTTPClient, HTTPRequest
from app.global_data.global_data import g


async def upload_artifact(short_url, file_body):
    config = g.get_central_config()
    base_url = config['nexus']['maven']['url']
    username = config['nexus']['maven']['username']
    password = config['nexus']['maven']['password']

    long_url = '{}/{}'.format(base_url, short_url)

    request = HTTPRequest(
        url=long_url,
        method='PUT',
        body=file_body,
        auth_username=username,
        auth_password=password,
        request_timeout=600
    )

    http = AsyncHTTPClient()
    try:
        res = await http.fetch(request)
    except Exception as e:
        return None

    return long_url if res.code == 201 else None


async def delete_artifact(url):
    config = g.get_central_config()
    username = config['nexus']['maven']['username']
    password = config['nexus']['maven']['password']

    request = HTTPRequest(
        url=url,
        method='DELETE',
        auth_username=username,
        auth_password=password,
        request_timeout=600
    )
    http = AsyncHTTPClient()
    try:
        await http.fetch(request)
    except Exception as e:
        pass


async def get_artifact(url):
    config = g.get_central_config()
    username = config['nexus']['maven']['username']
    password = config['nexus']['maven']['password']

    request = HTTPRequest(
        url=url,
        method='GET',
        auth_username=username,
        auth_password=password,
        request_timeout=600
    )
    http = AsyncHTTPClient()
    try:
        res = await http.fetch(request)
    except Exception as e:
        return None

    if res and res.code == 200:
        return str(res.body, encoding='utf-8')
    else:
        return None
