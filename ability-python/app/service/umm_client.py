from app.service.http_client import http_client


service_name = 'umm'


def find_ability(uuid, jwt=None):
    body = {
        'action': 'update_solution_call_count',
        'args': {
            'uuid': uuid,
        },
    }
    return http_client(service_name, body=body, jwt=jwt)
