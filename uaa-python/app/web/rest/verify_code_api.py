import tornado.web
from app.domain.verify_code import VerifyCode
from app.service import verify_code_service
from app.database import verify_code_db
from datetime import datetime, timedelta
import json


class VerifyCodeApi(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        verify_code = VerifyCode()
        verify_code.code = verify_code_service.gen_random_code()
        verify_code.expire = datetime.now() + timedelta(seconds=60)
        id = await verify_code_db.create_verify_code(verify_code)

        result = {
            'verifyId': id,
            'verifyCode': verify_code_service.gen_code_picture(verify_code.code)
        }

        self.write(result)

    async def post(self, *args, **kwargs):
        body = json.loads(str(self.request.body, encoding='utf-8'))
        result = await verify_code_service.validate_verify_code(body.get('verifyId'), body.get('verifyCode'))
        self.write(str(result))
