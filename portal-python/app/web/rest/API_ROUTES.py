from .health_check import HealthChecker
from .hello import Hello
from .error import Error
import tornado.web


API_ROUTES = [
    (r'/management/health', HealthChecker),
    (r'/api/hello', Hello),
    (r'/u(\w+)/(.*)', Error),
    (r'/p(\w+)/(.*)', Error),
    (r'/ability/(.*)', Error),
    (r'/(.*)', tornado.web.StaticFileHandler, {'path': 'app/web/www', 'default_filename': 'index.html'})

]