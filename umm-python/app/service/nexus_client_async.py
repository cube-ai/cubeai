import base64
from tornado.httpclient import AsyncHTTPClient, HTTPRequest
from app.globals.globals import g


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


async def delete_docker_image(url):
    image_server, image_id = url.split('/')
    image_name, image_tag = image_id.split(':')
    config = g.get_central_config()
    username = config['nexus']['docker']['registryUsername']
    password = config['nexus']['docker']['registryPassword']

    headers = {
        'Authorization': 'Basic {}'.format(str(base64.b64encode('{}:{}'.format(username, password).encode()), encoding='utf-8')),
        'Accept': 'application/vnd.docker.distribution.manifest.v2+json',
    }

    http = AsyncHTTPClient()
    get_url = 'https://{}/v2/{}/manifests/{}'.format(image_server, image_name, image_tag)
    request = HTTPRequest(
        url=get_url,
        method='GET',
        headers=headers
    )

    try:
        res = await http.fetch(request)
    except Exception as e:
        return

    digest = res.headers.get('Docker-Content-Digest')

    delete_url = 'https://{}/v2/{}/manifests/{}'.format(image_server, image_name, digest)
    request = HTTPRequest(
        url=delete_url,
        method='DELETE',
        headers=headers
    )
    try:
        await http.fetch(request)
    except Exception as e:
        pass
