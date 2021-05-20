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


def get_artifacts(solution_uuid, type, jwt=None):
    body = {
        'action': 'get_artifacts',
        'args': {
            'solutionUuid': solution_uuid,
            'type': type,
        },
    }
    return http_client(service_name, body=body, jwt=jwt)


def deploy_solution(solution, jwt):
    body = {
        'action': 'deploy_solution',
        'args': {
            'solutionId': solution.id,
            'deployer': solution.deployer,
            'deployStatus': solution.deployStatus,
            'k8sPort': solution.k8sPort,
        },
    }
    return http_client(service_name, body=body, jwt=jwt)


def update_solution_deploy_status(solutionId, status, jwt):
    body = {
        'action': 'update_solution_deploy_status',
        'args': {
            'solutionId': solutionId,
            'deployStatus': status,
        },
    }
    return http_client(service_name, body=body, jwt=jwt)
