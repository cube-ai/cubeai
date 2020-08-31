import requests
from app.global_data.global_data import g
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


def upload_artifact_data(short_url, data):
    config = g.get_central_config()
    base_url = config['nexus']['maven']['url']
    username = config['nexus']['maven']['username']
    password = config['nexus']['maven']['password']

    if isinstance(data, str):
        data = bytes(data, encoding='utf-8')

    long_url = '{}/{}'.format(base_url, short_url)

    try:
        res = requests.put(long_url, data=data, auth=(username, password))
    except Exception as e:
        logging.error(str(e))
        return None

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
