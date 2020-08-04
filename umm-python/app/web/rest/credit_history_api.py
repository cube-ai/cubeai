import tornado.web
from app.service import token_service
from app.database import credit_history_db
import json


class CreditHistoryApi(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        target_login = self.get_argument('targetLogin', None)  # 带 targetLogin 表示是管理员在查看
        pageable = {
            'page': self.get_argument('page', None),
            'size': self.get_argument('size', None),
            'sort': self.get_arguments('sort'),
        }

        token = token_service.get_token(self.request)
        user_login = token.username
        has_role = token.has_role('ROLE_ADMIN')

        if user_login is None or (target_login is not None and not has_role):
            self.send_error(403)
            return

        if target_login is not None:
            user_login = target_login

        where = 'WHERE user_login = "{}"'.format(user_login)

        total_count, result = await credit_history_db.get_credit_historys(where, pageable)
        self.set_header('X-Total-Count', total_count)

        self.write(json.dumps(result))
