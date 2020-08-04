import tornado.web
from app.domain.credit import Credit
from app.service import credit_service
from app.service import token_service
from app.database import credit_db
import json


class CreditApiA(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        has_role = token.has_role('ROLE_ADMIN')
        if not has_role:
            self.send_error(403)
            return

        user_login = self.get_argument('userLogin', None)
        result = await credit_db.get_credits(user_login)

        self.write(json.dumps(result))


class CreditApiB(tornado.web.RequestHandler):

    async def get(self, myself, *args, **kwargs):
        if myself != 'myself':
            self.send_error(400)
            return

        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        user_login = token.username
        credit = await credit_service.find_user_credit(user_login)

        self.write(credit)


class CreditApiC(tornado.web.RequestHandler):

    async def put(self, id, plus, *args, **kwargs):
        token = token_service.get_token(self.request)
        has_role = token.has_role('ROLE_ADMIN')
        if not has_role:
            self.send_error(403)
            return

        credit = Credit()
        credit.__dict__ = await credit_db.get_credit(id)
        await credit_service.update_credit(credit, plus, '管理员后台配置')

        self.finish()
