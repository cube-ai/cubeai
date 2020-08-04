import json
import tornado.web
from app.service import token_service
from app.service import user_service, passwd_service, verify_code_service, mail_service
from app.domain.user import User
from app.database import user_db


class AccountApi(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        if user_login is None:
            self.send_error(401)
            return

        user = await user_service.get_user_by_login(user_login)

        self.write(user.__dict__)

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username

        user = User()
        user.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))

        if user_login is None or user_login != user.login:
            self.send_error(403)
            return

        old = await user_db.find_one_by_kv('email', user.email)
        if old is not None and old.login != user_login:
            self.set_status(400)
            self.write('email已存在')
            return

        old = await user_db.find_one_by_kv('phone', user.phone)
        if old is not None and old.login != user_login:
            self.set_status(400)
            self.write('phone已存在')
            return

        old= await user_db.find_one_by_kv('login', user_login)
        if old is None:
            self.set_status(400)
            self.write('用户不存在')
            return

        await user_db.update_user_base_info(user)

        self.set_status(200)
        self.finish()


class ChangePasswordApi(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        if user_login is None:
            self.send_error(403)
            return

        password = str(self.request.body, encoding='utf-8')
        await user_db.change_password(user_login, passwd_service.encode(password))

        self.set_status(200)
        self.finish()


class RegisterApi(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        user = User()
        user.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))

        old= await user_db.find_one_by_kv('login', user.login)
        if old is not None:
            self.set_status(400)
            self.write('username重名')
            return

        old = await user_db.find_one_by_kv('email', user.email)
        if old is not None:
            self.set_status(400)
            self.write('email重名')
            return

        old = await user_db.find_one_by_kv('phone', user.phone)
        if old is not None:
            self.set_status(400)
            self.write('phone重名')
            return

        if not await user_service.register_user(user):
            self.set_status(400)
            self.write('发送注册邮件失败')
            return

        self.set_status(200)
        self.finish()


class ActivateApi(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        key = self.get_argument('key', None)

        if not await user_service.activate_registration(key):
            self.set_status(400)
            self.write('激活码失效')
            return

        self.set_status(200)
        self.finish()


class ResetPasswordApi(tornado.web.RequestHandler):

    async def post(self, action, *args, **kwargs):

        if action == 'init':
            body = json.loads(str(self.request.body, encoding='utf-8'))
            if await verify_code_service.validate_verify_code(int(body.get('verifyId')), body.get('verifyCode')) != 1:
                self.set_status(403)
                self.write('验证码错误')
                return

            reset_key = await user_service.request_password_reset(body.get('email'))
            if reset_key is None:
                self.set_status(400)
                self.write('用户不存在或未激活')
                return

            if not mail_service.send_password_reset_email(body.get('email'), body.get('resetUrlPrefix'), reset_key):
                self.set_status(400)
                self.write('发送邮件失败')
                return

            self.set_status(200)
            self.finish()

        elif action == 'finish':
            body = json.loads(str(self.request.body, encoding='utf-8'))
            if not await user_service.complete_password_reset(body.get('newPassword'), body.get('key')):
                self.set_status(400)
                self.write('重置码失效')
                return

            self.set_status(200)
            self.finish()
