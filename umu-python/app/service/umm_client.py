from app.service.http_client import http_client


service_name = 'umm'


def create_task(task, jwt):
    body = {
        'action': 'create_task',
        'args': {
            'task': task.__dict__,
        }
    }
    return http_client(service_name, body=body, jwt=jwt)


def update_task(task, jwt):
    body = {
        'action': 'update_task',
        'args': {
            'task': task.__dict__,
        }
    }
    return http_client(service_name, body=body, jwt=jwt)


def create_task_step(task_step, jwt):
    body = {
        'action': 'create_task_step',
        'args': {
            'task_step': task_step.__dict__,
        }
    }
    return http_client(service_name, body=body, jwt=jwt)


def create_solution(solution, jwt):
    body = {
        'action': 'create_solution',
        'args': {
            'solution': solution.__dict__,
        }
    }
    return http_client(service_name, body=body, jwt=jwt)


def get_solutions(uuid, jwt=None):
    body = {
        'action': 'get_solutions',
        'args': {
            'uuid': uuid,
        }
    }
    return http_client(service_name, body=body, jwt=jwt)


def delete_solution(solutionId, jwt):
    body = {
        'action': 'delete_solution',
        'args': {
            'solutionId': solutionId,
        }
    }
    return http_client(service_name, body=body, jwt=jwt)


def create_artifact(artifact, jwt):
    body = {
        'action': 'create_artifact',
        'args': {
            'artifact': artifact.__dict__,
        }
    }
    return http_client(service_name, body=body, jwt=jwt)


def get_artifacts(solution_uuid, jwt=None):
    body = {
        'action': 'get_artifacts',
        'args': {
            'solutionUuid': solution_uuid,
        }
    }
    return http_client(service_name, body=body, jwt=jwt)


def delete_artifact(artifactId, jwt):
    body = {
        'action': 'delete_artifact',
        'args': {
            'artifactId': artifactId,
        }
    }
    return http_client(service_name, body=body, jwt=jwt)


def create_document(document, jwt):
    body = {
        'action': 'create_document',
        'args': {
            'document': document.__dict__,
        }
    }
    return http_client(service_name, body=body, jwt=jwt)


def find_document(documentId, jwt=None):
    body = {
        'action': 'find_document',
        'args': {
            'documentId': documentId,
        }
    }
    return http_client(service_name, body=body, jwt=jwt)


def delete_document(documentId, jwt):
    body = {
        'action': 'delete_document',
        'args': {
            'documentId': documentId,
        }
    }
    return http_client(service_name, body=body, jwt=jwt)
