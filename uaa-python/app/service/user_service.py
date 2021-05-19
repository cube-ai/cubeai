from app.domain.user import User
from app.database import user_db
from app.service import passwd_service, token_service
from app.utils import mytime


def create_user(**args):
    token = token_service.get_token(args.get('http_request'))
    user_login = token.username
    has_role = token.has_role('ROLE_ADMIN')
    if not has_role:
        raise Exception('403 Forbidden')

    user = User()
    user.__dict__ = args.get('user')

    old = user_db.find_one_by_kv('login', user.login)
    if old is not None:
        raise Exception('400 已存在同名用户')

    old = user_db.find_one_by_kv('email', user.email)
    if old is not None:
        raise Exception('400 email已存在')

    old = user_db.find_one_by_kv('phone', user.phone)
    if old is not None:
        raise Exception('400 phone已存在')

    user.password = passwd_service.encode(user.password)
    user.activated = True
    user.imageUrl = ''
    user.langKey = 'en'
    user.createdBy = user_login
    user.createdDate = mytime.now()
    user.lastModifiedBy = user.createdBy
    user.lastModifiedDate = mytime.now()

    id = user_db.create_user(user)
    return id


def update_user(**args):
    token = token_service.get_token(args.get('http_request'))
    user_login = token.username
    has_role = token.has_role('ROLE_ADMIN')
    if not has_role:
        raise Exception('403 Forbidden')

    user = User()
    user.__dict__ = args.get('user')

    old = user_db.find_one_by_kv('email', user.email)
    if old is not None and old.id != user.id:
        raise Exception('400 email已存在')

    old = user_db.find_one_by_kv('phone', user.phone)
    if old is not None and old.id != user.id:
        raise Exception('400 phone已存在')

    user.lastModifiedBy = user_login
    user.lastModifiedDate = mytime.now()
    user_db.update_user(user)

    if hasattr(user, 'password') and user.password:
        user_db.change_password(user.login, passwd_service.encode(user.password))

    return 0


def get_users(**args):
    http_request = args.get('http_request')
    token = token_service.get_token(http_request)
    has_role = token.has_role('ROLE_ADMIN')
    if not has_role:
        raise Exception('403 Forbidden')

    filter = args.get('filter')
    pageable = {
        'page': args.get('page'),
        'size': args.get('size'),
        'sort': args.get('sort'),
    }

    where = ''
    if filter is not None:
        where += 'login like "%{}%"'.format(filter)
        where += ' or full_name like "%{}%"'.format(filter)
        where += ' or email like "%{}%"'.format(filter)
        where += ' or phone like "%{}%"'.format(filter)

    if where != '':
        where = 'WHERE ' + where

    total, results = user_db.get_users(where, pageable)
    return {
        'total': total,
        'results': results,
    }


def find_user(login, http_request):
    token = token_service.get_token(http_request)
    user_login = token.username
    has_role = token.has_role('ROLE_ADMIN')
    if not has_role and not login == user_login and not user_login == 'internal':
        raise Exception('403 Forbidden')

    user = get_user_by_login(login)

    if user is None:
        raise Exception('404 user not found')

    return user.__dict__


def delete_user(login, http_request):
    token = token_service.get_token(http_request)
    has_role = token.has_role('ROLE_ADMIN')
    if not has_role:
        raise Exception('403 Forbidden')

    user_db.delete_user_by_login(login)
    return 0


def get_login_exist(**args):
    login = args.get('login')

    if login.startswith('admin') or login.startswith('internal') or login.startswith('system') or login.startswith('root'):
        return 1

    user = get_user_by_login(login)
    return 0 if user is None else 1


def get_email_exist(**args):
    email = args.get('email')

    user = get_user_by_email(email)
    return 0 if user is None else 1


def get_phone_exist(**args):
    phone = args.get('phone')

    user = get_user_by_phone(phone)
    return 0 if user is None else 1


def get_user_by_id(id):
    user = user_db.find_one_by_kv('id', id)
    if user is not None:
        user.remove_internal_values()
    return user


def get_user_by_login(login):
    user = user_db.find_one_by_kv('login', login)
    if user is not None:
        user.remove_internal_values()
    return user


def get_user_by_email(email):
    user = user_db.find_one_by_kv('email', email)
    if user is not None:
        user.remove_internal_values()
    return user


def get_user_by_phone(phone):
    user = user_db.find_one_by_kv('phone', phone)
    if user is not None:
        user.remove_internal_values()
    return user

