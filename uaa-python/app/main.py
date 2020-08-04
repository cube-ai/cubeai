import tornado.web
import tornado.ioloop
import tornado.httpserver
from apscheduler.schedulers.tornado import TornadoScheduler
from app.web.rest.API_ROUTES import API_ROUTES
from app.globals.globals import g
from app.service import scheduler_service
import logging


def main():
    g.load_globals_data()
    if not g.init_success:
        return

    scheduler = TornadoScheduler()
    scheduler.add_job(scheduler_service.do_every_day, 'cron', day_of_week='0-6', hour=0, minute=30)
    scheduler.start()

    app = tornado.web.Application(
        API_ROUTES,
        cookie_secret=g.config.cookie_secret,
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
