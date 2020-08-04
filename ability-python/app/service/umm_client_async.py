from app.service.http_client_async import async_http_client


service_name = 'umm'


async def get_deployment(uuid):
    return await async_http_client('get', service_name, '/model/ability?uuid={}'.format(uuid), jwt=None)
