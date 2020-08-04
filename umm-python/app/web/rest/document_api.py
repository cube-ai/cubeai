import tornado.web
from app.domain.document import Document
from app.service import token_service
from app.database import document_db, solution_db
from app.utils import mytime
import json

class DocumentApiA(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        document = Document()
        document.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))
        solutions = await solution_db.get_solutions_by_uuid(document.solutionUuid)
        solution = solutions[0]

        user_login = token.username
        if user_login != solution.get('authorLogin'):
            self.send_error(403)
            return

        document.authorLogin = user_login
        document.createdDate = mytime.now()
        document.modifiedDate = mytime.now()
        await document_db.create_document(document)
        self.set_status(201)
        self.finish()

    async def get(self, *args, **kwargs):
        solution_uuid = self.get_argument('solutionUuid', None)
        name = self.get_argument('name', None)

        where = 'WHERE solution_uuid = "{}"'.format(solution_uuid)
        if name is not None:
            where += ' and name = "{}" '.format(name)

        result = await document_db.get_documents(where)
        self.write(json.dumps(result))


class DocumentApiB(tornado.web.RequestHandler):

    async def get(self, id, *args, **kwargs):
        result = await document_db.get_document(id)
        self.write(result)

    async def delete(self, id, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        user_login = token.username
        has_role = token.has_role('ROLE_MANAGER')

        document = await document_db.get_document(id)

        if user_login != document.get('authorLogin') and not has_role:
            self.send_error(403)
            return

        await document_db.delete_document(id)
        self.set_status(200)
        self.finish()
