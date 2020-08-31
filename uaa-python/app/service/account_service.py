from datetime import datetime
from app.domain.user import User
from app.database import user_db
from app.service import mail_service, passwd_service, token_service, verify_code_service, user_service
from app.utils import mytime


def get_current_account(**args):
    http_request = args.get('http_request')
    token = token_service.get_token(http_request)
    user_login = token.username
    if user_login is None:
        raise Exception('401 Unauthorized')

    user = user_service.get_user_by_login(user_login)

    return user.__dict__


def update_current_account(**args):
    http_request = args.get('http_request')
    token = token_service.get_token(http_request)
    user_login = token.username

    user = User()
    user.__dict__ = args.get('user')

    if user_login is None or user_login != user.login:
        raise Exception('403 Forbidden')

    old = user_db.find_one_by_kv('email', user.email)
    if old is not None and old.login != user_login:
        raise Exception('400 email已存在')

    old = user_db.find_one_by_kv('phone', user.phone)
    if old is not None and old.login != user_login:
        raise Exception('400 phone已存在')

    old = user_db.find_one_by_kv('login', user_login)
    if old is None:
        raise Exception('400 用户不存在')

    user_db.update_user_base_info(user)

    return 0


def change_password(**args):
    http_request = args.get('http_request')
    token = token_service.get_token(http_request)
    user_login = token.username

    if user_login is None:
        raise Exception('403 Forbidden')

    password = args.get('password')
    user_db.change_password(user_login, passwd_service.encode(password))

    return 0


def register_user(**args):
    user = User()
    user.__dict__ = args.get('user')

    old = user_db.find_one_by_kv('login', user.login)
    if old is not None:
        raise Exception('400 username重名')

    old = user_db.find_one_by_kv('email', user.email)
    if old is not None:
        raise Exception('400 email重名')

    old = user_db.find_one_by_kv('phone', user.phone)
    if old is not None:
        raise Exception('400 phone重名')

    user.password = passwd_service.encode(user.password)
    user.activated = False
    user.activationKey = passwd_service.gen_random_key()
    user.imageUrl = ''
    user.langKey = 'en'
    user.createdBy = user.login
    user.createdDate = mytime.now()
    user.lastModifiedBy = user.login
    user.lastModifiedDate = mytime.now()
    user.authorities = ['ROLE_USER', ]  # 缺省所有用户都有ROLE_USER角色

    user_db.create_user(user)
    if not mail_service.send_activation_email(user.email, user.activateUrlPrefix, user.activationKey):
        user_db.delete_user_by_login(user.login)
        raise Exception('400 发送注册邮件失败')

    return 0


def activate_user(**args):
    key = args.get('key')

    user = user_db.find_one_by_kv('activation_key', key)
    if user is None:
        raise Exception('400 激活码失效')

    user.activated = True
    user.activationKey = ''
    user_db.update_user_activation(user)

    return 0


def password_reset_init(**args):
    if verify_code_service.validate_verify_code(**args) != 1:
        raise Exception('403, 验证码错误')

    user = user_db.find_one_by_kv('email', args.get('email'))
    if user is None or not user.activated:
        raise Exception('400, 用户不存在或未激活')

    user.resetKey = passwd_service.gen_random_key()
    user.resetDate = mytime.now()
    user_db.update_user_password_reset(user)

    if not mail_service.send_password_reset_email(args.get('email'), args.get('resetUrlPrefix'), user.resetKey):
        raise Exception('400, 发送邮件失败')

    return 0


def password_reset_finish(**args):
    new_password = args.get('newPassword')
    key = args.get('key')

    user = user_db.find_one_by_kv('reset_key', key)
    if user is None:
        raise Exception('400, 重置码失效')

    expire = datetime.strptime(user.resetDate, '%Y-%m-%dT%H:%M:%S').timestamp() + 172800  # 2 days
    if datetime.now().timestamp() > expire:
        raise Exception('400, 重置码失效')

    user.password = passwd_service.encode(new_password)
    user.resetKey = ''
    user_db.update_user_password_reset(user)
    return 0
