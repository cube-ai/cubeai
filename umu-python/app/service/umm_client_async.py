import json
from app.service.http_client_async import async_http_client


service_name = 'umm'


async def create_task(task, jwt):
    return await async_http_client('post', service_name, '/api/tasks', body=json.dumps(task.__dict__), jwt=jwt)


async def update_task(task, jwt):
    return await async_http_client('put', service_name, '/api/tasks', body=json.dumps(task.__dict__), jwt=jwt)


async def get_tasks(uuid, jwt):
    return await async_http_client('get', service_name, '/api/tasks?uuid={}'.format(uuid), jwt=jwt)


async def create_task_step(task_step, jwt):
    return await async_http_client('post', service_name, '/api/task-steps', body=json.dumps(task_step.__dict__), jwt=jwt)


async def get_solutions(uuid, jwt=None):
    return await async_http_client('get', service_name, '/api/solutions?uuid={}'.format(uuid), jwt=jwt)


async def create_solution(solution, jwt):
    return await async_http_client('post', service_name, '/api/solutions', body=json.dumps(solution.__dict__), jwt=jwt)


async def delete_solution(id, jwt):
    return await async_http_client('delete', service_name, '/api/solutions/{}'.format(id), jwt=jwt)


async def create_artifact(artifact, jwt):
    return await async_http_client('post', service_name, '/api/artifacts', body=json.dumps(artifact.__dict__), jwt=jwt)


async def get_all_artifacts(solution_uuid, jwt=None):
    return await async_http_client('get', service_name, '/api/artifacts?solutionUuid={}'.format(solution_uuid), jwt=jwt)


async def delete_artifact(id, jwt):
    return await async_http_client('delete', service_name, '/api/artifacts/{}'.format(id), jwt=jwt)


async def create_document(document, jwt):
    return await async_http_client('post', service_name, '/api/documents', body=json.dumps(document.__dict__), jwt=jwt)


async def get_document(id, jwt=None):
    return await async_http_client('get', service_name, '/api/documents/{}'.format(id), jwt=jwt)


async def delete_document(id, jwt):
    return await async_http_client('delete', service_name, '/api/documents/{}'.format(id), jwt=jwt)
