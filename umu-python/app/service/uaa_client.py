from app.service.http_client import http_client


service_name = 'uaa'


def find_user(login, jwt):
    body = {
        'action': 'find_user',
        'args': {
            'login': login,
        }
    }
    return http_client(service_name, body=body, jwt=jwt)


def send_message(message, jwt):
    body = {
        'action': 'send_message',
        'args': {
            'message': message,
        }
    }
    return http_client(service_name, body=body, jwt=jwt)

