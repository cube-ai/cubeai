import json
from app.service.http_client import http_client


service_name = 'umm'


def create_task(task, jwt):
    return http_client('post', service_name, '/api/tasks', body=json.dumps(task.__dict__), jwt=jwt)


def update_task(task, jwt):
    return http_client('put', service_name, '/api/tasks', body=json.dumps(task.__dict__), jwt=jwt)


def get_tasks(uuid, jwt):
    return http_client('get', service_name, '/api/tasks?uuid={}'.format(uuid), jwt=jwt)


def create_task_step(task_step, jwt):
    return http_client('post', service_name, '/api/task-steps', body=json.dumps(task_step.__dict__), jwt=jwt)


def delete_task_steps(body, jwt):
    return http_client('put', service_name, '/api/task-steps', body=json.dumps(body), jwt=jwt)


def get_solutions(uuid, jwt=None):
    return http_client('get', service_name, '/api/solutions?uuid={}'.format(uuid), jwt=jwt)


def create_solution(solution, jwt):
    return http_client('post', service_name, '/api/solutions', body=json.dumps(solution.__dict__), jwt=jwt)


def delete_solution(id, jwt):
    return http_client('delete', service_name, '/api/solutions/{}'.format(id), jwt=jwt)


def create_artifact(artifact, jwt):
    return http_client('post', service_name, '/api/artifacts', body=json.dumps(artifact.__dict__), jwt=jwt)


def get_artifacts(solution_uuid, artifact_type, jwt=None):
    return http_client('get', service_name, '/api/artifacts?solutionUuid={}&type={}'.format(solution_uuid, artifact_type), jwt=jwt)


def get_all_artifacts(solution_uuid, jwt=None):
    return http_client('get', service_name, '/api/artifacts?solutionUuid={}'.format(solution_uuid), jwt=jwt)


def delete_artifact(id, jwt):
    return http_client('delete', service_name, '/api/artifacts/{}'.format(id), jwt=jwt)


def create_document(document, jwt):
    return http_client('post', service_name, '/api/documents', body=json.dumps(document.__dict__), jwt=jwt)


def get_document(id, jwt=None):
    return http_client('get', service_name, '/api/documents/{}'.format(id), jwt=jwt)


def delete_document(id, jwt):
    return http_client('delete', service_name, '/api/documents/{}'.format(id), jwt=jwt)


def create_deployment(deployment, jwt):
    return http_client('post', service_name, '/api/deployments', body=json.dumps(deployment.__dict__), jwt=jwt)


def update_deployment_status(body, jwt):
    return http_client('put', service_name, '/api/deployments/status', body=json.dumps(body), jwt=jwt)
