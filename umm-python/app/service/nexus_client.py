import requests
import base64
from app.globals.globals import g
import logging


def upload_artifact(short_url, file_path):
    config = g.get_central_config()
    base_url = config['nexus']['maven']['url']
    username = config['nexus']['maven']['username']
    password = config['nexus']['maven']['password']

    long_url = '{}/{}'.format(base_url, short_url)

    with open(file_path, "rb") as file:
        try:
            res = requests.put(long_url, data=file, auth=(username, password))
        except Exception as e:
            logging.DEBUG(e)

    return long_url if res.status_code == 201 else None


def delete_artifact(url):
    config = g.get_central_config()
    username = config['nexus']['maven']['username']
    password = config['nexus']['maven']['password']

    try:
        res = requests.delete(url, auth=(username, password))
    except Exception as e:
        logging.DEBUG(e)


def get_artifact(url):
    config = g.get_central_config()
    username = config['nexus']['maven']['username']
    password = config['nexus']['maven']['password']

    try:
        res = requests.get(url, auth=(username, password))
    except Exception as e:
        logging.DEBUG(e)
        return None

    if res and res.status_code == 200:
        return str(res.content, encoding='utf-8')
    else:
        return None


def delete_docker_image(url):
    image_server, image_id = url.split('/')
    image_name, image_tag = image_id.split(':')
    config = g.get_central_config()
    username = config['nexus']['docker']['registryUsername']
    password = config['nexus']['docker']['registryPassword']

    headers = {
        'Authorization': 'Basic {}'.format(str(base64.b64encode('{}:{}'.format(username, password).encode()), encoding='utf-8')),
        'Accept': 'application/vnd.docker.distribution.manifest.v2+json',
    }

    get_url = 'https://{}/v2/{}/manifests/{}'.format(image_server, image_name, image_tag)
    res = requests.get(url=get_url, headers=headers)
    digest = res.headers.get('Docker-Content-Digest')

    delete_url = 'https://{}/v2/{}/manifests/{}'.format(image_server, image_name, digest)
    requests.delete(url=delete_url, headers=headers)
