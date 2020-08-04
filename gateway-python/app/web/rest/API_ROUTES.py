from .health_check import HealthChecker
from .hello import Hello
from .auth_api import AuthApi
from .gateway_api import GatewayApi


API_ROUTES = [
    (r'/management/health', HealthChecker),
    (r'/api/hello', Hello),
    (r'/auth/(\w+)', AuthApi),
    (r'/(.*)', GatewayApi),
]