import tornado.web
from app.domain.description import Description
from app.service import token_service
from app.database import description_db
import json


class DescriptionApiA(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        solution_uuid = self.get_argument('solutionUuid', None)

        result = await description_db.get_descriptions(solution_uuid)
        self.write(json.dumps(result))


class DescriptionApiB(tornado.web.RequestHandler):

    async def put(self, content, *args, **kwargs):
        if content != 'content':
            self.send_error(400)
            return

        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        user_login = token.username
        has_role = token.has_role('ROLE_MANAGER')

        body = json.loads(str(self.request.body, encoding='utf-8'))
        id = body.get('id')
        description = Description()
        description.__dict__ = await description_db.get_description(id)

        if user_login != description.authorLogin and not has_role:
            self.send_error(403)
            return

        description.content = body.get('content')
        await description_db.update_description_content(description)
        self.set_status(201)
        self.finish()

    async def delete(self, id, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        userLogin = token.username
        has_role = token.has_role('ROLE_ADMIN')

        description = await description_db.get_description(id)
        if userLogin != description.get('authorLogin') and not has_role:
            self.send_error(403)
            return

        await description_db.delete_description(id)
        self.set_status(200)
        self.finish()
