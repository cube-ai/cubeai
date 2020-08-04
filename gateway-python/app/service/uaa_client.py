from app.service.http_client_async import async_http_client


service_name = 'uaa'


async def validate_verify_code(body):
    return await async_http_client('POST', service_name, '/api/verify-codes', body=body, jwt=None)
