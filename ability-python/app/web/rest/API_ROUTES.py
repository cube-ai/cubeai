from .health_check import HealthChecker
from .hello import Hello
from .model_api import ModelApi
from .stream_api import StreamApi
from .file_api import FileApi
from .web_api import WebApi


API_ROUTES = [
    (r'/management/health', HealthChecker),
    (r'/api/hello', Hello),
    (r'/model/(\w+)', ModelApi),
    (r'/stream/(\w+)/(\w+)', StreamApi),
    (r'/file/(\w+)/(\w+)', FileApi),
    (r'/web/(\w+)/(.*)', WebApi),
]