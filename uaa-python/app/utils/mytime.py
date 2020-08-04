from datetime import datetime, timedelta
from app.globals.globals import g


def now():
    now_time = datetime.now()
    if g.config.env == 'prod':  # 目前在docker中都采用的时UTC，所以应转成北京时间
        now_time += timedelta(hours=8)
    return now_time.strftime('%Y-%m-%dT%H:%M:%S')
