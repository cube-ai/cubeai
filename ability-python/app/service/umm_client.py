from app.service.http_client import http_client


service_name = 'umm'


def find_and_call_ability(uuid, jwt=None):
    body = {
        'action': 'find_and_call_ability',
        'args': {
            'uuid': uuid,
        },
    }
    return http_client(service_name, body=body, jwt=jwt)
