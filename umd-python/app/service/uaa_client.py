import json
from app.service.http_client import http_client


service_name = 'uaa'


def send_message(message, jwt):
    return http_client('post', service_name, '/api/messages/send', body=json.dumps(message), jwt=jwt)
