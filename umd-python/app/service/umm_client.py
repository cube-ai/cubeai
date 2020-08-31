import json
from app.service.http_client import http_client


service_name = 'umm'


def create_task(task, jwt):
    body = {
        'action': 'create_task',
        'args': {
            'task': task.__dict__,
        },
    }
    return http_client(service_name, body=body, jwt=jwt)


def update_task(task, jwt):
    body = {
        'action': 'update_task',
        'args': {
            'task': task.__dict__,
        },
    }
    return http_client(service_name, body=body, jwt=jwt)


def create_task_step(task_step, jwt):
    body = {
        'action': 'create_task_step',
        'args': {
            'task_step': task_step.__dict__,
        },
    }
    return http_client(service_name, body=body, jwt=jwt)


def delete_task_steps(task_uuid, start_progress, end_progress, jwt):
    body = {
        'action': 'delete_task_steps',
        'args': {
            'task_uuid': task_uuid,
            'start_progress': start_progress,
            'end_progress': end_progress,
        },
    }
    return http_client(service_name, body=body, jwt=jwt)


def get_solutions(uuid, jwt=None):
    body = {
        'action': 'get_solutions',
        'args': {
            'uuid': uuid,
        },
    }
    return http_client(service_name, body=body, jwt=jwt)


def create_solution(solution, jwt):
    return http_client('post', service_name, '/api/solutions', body=json.dumps(solution.__dict__), jwt=jwt)


def delete_solution(id, jwt):
    return http_client('delete', service_name, '/api/solutions/{}'.format(id), jwt=jwt)


def create_artifact(artifact, jwt):
    return http_client('post', service_name, '/api/artifacts', body=json.dumps(artifact.__dict__), jwt=jwt)


def get_artifacts(solution_uuid, type, jwt=None):
    body = {
        'action': 'get_artifacts',
        'args': {
            'solutionUuid': solution_uuid,
            'type': type,
        },
    }
    return http_client(service_name, body=body, jwt=jwt)


def delete_artifact(id, jwt):
    return http_client('delete', service_name, '/api/artifacts/{}'.format(id), jwt=jwt)


def create_document(document, jwt):
    return http_client('post', service_name, '/api/documents', body=json.dumps(document.__dict__), jwt=jwt)


def get_document(id, jwt=None):
    return http_client('get', service_name, '/api/documents/{}'.format(id), jwt=jwt)


def delete_document(id, jwt):
    return http_client('delete', service_name, '/api/documents/{}'.format(id), jwt=jwt)


def create_deployment(deployment, jwt):
    body = {
        'action': 'create_deployment',
        'args': {
            'deployment': deployment.__dict__,
        },
    }
    return http_client(service_name, body=body, jwt=jwt)


def update_deployment_status(deploymentId, status, jwt):
    body = {
        'action': 'update_deployment_status',
        'args': {
            'deploymentId': deploymentId,
            'status': status,
        },
    }
    return http_client(service_name, body=body, jwt=jwt)
