import tornado.web
import tornado.ioloop
import tornado.httpserver
from app.web.rest.API_ROUTES import API_ROUTES
from app.globals.globals import g
import logging


def main():
    g.load_globals_data()
    if not g.init_success:
        return

    app = tornado.web.Application(
        handlers=API_ROUTES,
        cookie_secret=g.config.cookie_secret,
        debug=('dev' == g.config.env)
    )
    http_server = tornado.httpserver.HTTPServer(app, max_buffer_size=800*1024*1024)
    http_server.listen(g.config.server_port)

    logging.critical('#######################################################################')
    logging.critical('    {} service started.'.format(g.config.app_name.upper()))
    logging.critical('    Listening at: {}:{}'.format(g.config.server_ip, g.config.server_port))
    logging.critical('#######################################################################')
    tornado.ioloop.IOLoop.current().start()


if __name__ == "__main__":
    main()
