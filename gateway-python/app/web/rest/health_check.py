import tornado.web


class HealthChecker(tornado.web.RequestHandler):
    def get(self, *args, **kwargs):
        self.write('{"description": "Consul Discovery Client", "status": "UP"}')
