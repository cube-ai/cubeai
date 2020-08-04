from .health_check import HealthChecker
from .hello import Hello
from .ability_api import AbilityApi


API_ROUTES = [
    (r'/management/health', HealthChecker),
    (r'/api/hello', Hello),
    (r'/model/(\w+)/(\w+)', AbilityApi),

]