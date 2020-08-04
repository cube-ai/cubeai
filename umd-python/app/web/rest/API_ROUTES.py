from .health_check import HealthChecker
from .hello import Hello
from .deploy_api import DeployApi
from .lcm_api import LcmApi


API_ROUTES = [
    (r'/management/health', HealthChecker),
    (r'/api/hello', Hello),
    (r'/api/deploy', DeployApi),
    (r'/api/lcm/(\w+)', LcmApi),

]