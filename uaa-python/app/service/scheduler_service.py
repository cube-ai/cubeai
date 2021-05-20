from datetime import datetime, timedelta
from app.database import user_db, verify_code_db


def do_every_day():
    cleanup_not_activated_users()
    cleanup_expired_verify_codes()


def cleanup_not_activated_users():
    begin = '2020-01-01T00:00:00'
    end = (datetime.now() - timedelta(days=3)).strftime('%Y-%m-%dT%H:%M:%S')
    where = 'WHERE activated = 0 and created_date between "{}" and "{}"'.format(begin, end)

    users = user_db.find_any_users(where)

    for user in users:
        user_db.delete_user_by_id(user.id)


def cleanup_expired_verify_codes():
    verify_code_db.cleanup_all()
