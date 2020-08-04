from datetime import datetime
from app.database import user_db
from app.service import mail_service, passwd_service
from app.utils import mytime


async def activate_registration(key):
    user = await user_db.find_one_by_kv('activation_key', key)
    if user is None:
        return False

    user.activated = True
    user.activationKey = ''
    await user_db.update_user_activation(user)
    return True


async def complete_password_reset(new_password, key):
    user = await user_db.find_one_by_kv('reset_key',key)
    if user is None:
        return False

    expire = datetime.strptime(user.resetDate, '%Y-%m-%dT%H:%M:%S').timestamp() + 172800  # 2 days
    if datetime.now().timestamp() > expire:
        return False

    user.password = passwd_service.encode(new_password)
    user.resetKey = ''
    await user_db.update_user_password_reset(user)
    return True


async def request_password_reset(email):
    user = await user_db.find_one_by_kv('email', email)
    if user is None or not user.activated:
        return None

    user.resetKey = passwd_service.gen_random_key()
    user.resetDate = mytime.now()
    await user_db.update_user_password_reset(user)
    return user.resetKey


async def register_user(user):
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

    await user_db.create_user(user)
    if not mail_service.send_activation_email(user.email, user.activateUrlPrefix, user.activationKey):
        await user_db.delete_user_by_login(user.login)
        return False
    else:
        return True


async def create_user(user):
    user.password = passwd_service.encode(user.password)
    user.activated = True
    user.imageUrl = ''
    user.langKey = 'en'
    user.createdDate = mytime.now()
    user.lastModifiedBy = user.createdBy
    user.lastModifiedDate = mytime.now()

    await user_db.create_user(user)


async def update_user(user):
    user.lastModifiedDate = mytime.now()
    await user_db.update_user(user)

    if hasattr(user, 'password') and user.password:
        await user_db.change_password(user.login, passwd_service.encode(user.password))


async def get_user_by_id(id):
    user = await user_db.find_one_by_kv('id', id)
    if user is not None:
        user.remove_internal_values()
    return user


async def get_user_by_login(login):
    user = await user_db.find_one_by_kv('login', login)
    if user is not None:
        user.remove_internal_values()
    return user


async def get_user_by_email(email):
    user = await user_db.find_one_by_kv('email', email)
    if user is not None:
        user.remove_internal_values()
    return user


async def get_user_by_phone(phone):
    user = await user_db.find_one_by_kv('phone', phone)
    if user is not None:
        user.remove_internal_values()
    return user
