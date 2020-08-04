import tornado.web


class Error(tornado.web.RequestHandler):

    def get(self, *args, **kwargs):
        self.set_status(500)
        self.write('服务维护中...')

