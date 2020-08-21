from .health_check import HealthChecker
from .hello import Hello
import tornado.web


API_ROUTES = [
    (r'/management/health', HealthChecker),
    (r'/api/hello', Hello),
    (r'/(.*)', tornado.web.StaticFileHandler, {'path': 'app/web/www', 'default_filename': 'index.html'})

]