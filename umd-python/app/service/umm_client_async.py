import json
from app.service.http_client_async import async_http_client


service_name = 'umm'


async def create_task(task, jwt):
    return await async_http_client('post', service_name, '/api/tasks', body=json.dumps(task.__dict__), jwt=jwt)


async def get_solutions(uuid, jwt=None):
    return await async_http_client('get', service_name, '/api/solutions?uuid={}'.format(uuid), jwt=jwt)
