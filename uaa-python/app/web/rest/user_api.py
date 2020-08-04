import json
import tornado.web
from app.service import token_service
from app.service import user_service
from app.domain.user import User
from app.database import user_db


class UserApiA(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        has_role = token.has_role('ROLE_ADMIN')
        if not has_role:
            self.send_error(403)
            return

        user = User()
        user.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))

        old= await user_db.find_one_by_kv('login', user.login)
        if old is not None:
            self.set_status(400)
            self.write('已存在同名用户')
            return

        old = await user_db.find_one_by_kv('email', user.email)
        if old is not None:
            self.set_status(400)
            self.write('email已存在')
            return

        old = await user_db.find_one_by_kv('phone', user.phone)
        if old is not None:
            self.set_status(400)
            self.write('phone已存在')
            return

        user.createdBy = user_login
        await user_service.create_user(user)

        self.set_status(201)
        self.finish()

    async def put(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        has_role = token.has_role('ROLE_ADMIN')
        if not has_role:
            self.send_error(403)
            return

        user = User()
        user.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))

        old = await user_db.find_one_by_kv('email', user.email)
        if old is not None and old.id != user.id:
            self.set_status(400)
            self.write('email已存在')
            return

        old = await user_db.find_one_by_kv('phone', user.phone)
        if old is not None and old.id != user.id:
            self.set_status(400)
            self.write('phone已存在')
            return

        user.lastModifiedBy = user_login
        await user_service.update_user(user)

        self.set_status(200)
        self.finish()

    async def get(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        has_role = token.has_role('ROLE_ADMIN')
        if not has_role:
            self.send_error(403)
            return

        pageable = {
            'page': self.get_argument('page', None),
            'size': self.get_argument('size', None),
            'sort': self.get_arguments('sort'),
        }

        total_count, result = await user_db.get_users('', pageable)
        self.set_header('X-Total-Count', total_count)

        self.write(json.dumps(result))


class UserApiB(tornado.web.RequestHandler):

    async def get(self, login,  *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        has_role = token.has_role('ROLE_ADMIN')
        if not has_role and not login == user_login and not user_login == 'internal':
            self.send_error(403)
            return

        user = await user_service.get_user_by_login(login)

        if user is None:
            self.send_error(404)
            return

        self.write(user.__dict__)

    async def delete(self, login, *args, **kwargs):
        token = token_service.get_token(self.request)
        has_role = token.has_role('ROLE_ADMIN')
        if not has_role:
            self.send_error(403)
            return

        await user_db.delete_user_by_login(login)
        self.finish()


class UserExistLoginApi(tornado.web.RequestHandler):

    async def get(self, login,  *args, **kwargs):
        if login.find('admin') == 0 or login.find('internal') == 0 or login.find('system') == 0 or login.find('root') == 0:
            self.write('1')

        user = await user_service.get_user_by_login(login)
        self.write('0' if user is None else '1')


class UserExistEmailApi(tornado.web.RequestHandler):

    async def get(self, email, *args, **kwargs):
        user = await user_service.get_user_by_email(email)
        self.write('0' if user is None else '1')


class UserExistPhoneApi(tornado.web.RequestHandler):

    async def get(self, phone, *args, **kwargs):
        user = await user_service.get_user_by_phone(phone)
        self.write('0' if user is None else '1')
