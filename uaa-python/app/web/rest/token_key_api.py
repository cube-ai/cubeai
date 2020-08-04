import tornado.web
from app.globals.globals import g


class TokenKeyApi(tornado.web.RequestHandler):

    def get(self, *args, **kwargs):
        result = {
            'alg': 'SHA256withRSA',
            'value': str(g.config.public_key, encoding='utf-8')
        }

        self.write(result)
