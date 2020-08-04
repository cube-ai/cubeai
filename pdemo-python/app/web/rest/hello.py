import tornado.web


class Hello(tornado.web.RequestHandler):

    def get(self, *args, **kwargs):
        self.write('Hello world!')

