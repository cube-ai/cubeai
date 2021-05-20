from datetime import datetime, timedelta
from app.global_data.global_data import g


def now():
    now_time = datetime.now()
    if g.config.app_profile == 'prod':  # 目前在docker中都采用的时UTC，所以应转成北京时间
        now_time += timedelta(hours=8)
    return now_time.strftime('%Y-%m-%dT%H:%M:%S')
