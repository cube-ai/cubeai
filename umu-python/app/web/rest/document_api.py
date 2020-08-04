import tornado.web
from app.service import token_service
from app.service import umm_client_async
from app.service import nexus_client_async
from app.domain.solution import Solution
from app.domain.document import Document
import logging


class DocumentApi(tornado.web.RequestHandler):

    async def post(self, solution_uuid, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            logging.info('JWT验证失败')
            self.send_error(403)
            return

        user_login = token.username

        solution_list = await umm_client_async.get_solutions(solution_uuid, token.jwt)
        if solution_list and len(solution_list) > 0:
            solution = Solution()
            solution.__dict__ = solution_list[0]
        else:
            logging.info('文档模型不存在')
            self.send_error(400)
            return

        if solution.authorLogin != user_login:
            logging.info('无权访问该模型')
            self.send_error(403)
            return

        try:
            file_obj = self.request.files.get('document')[0]
            filename = file_obj.filename
            filebody = file_obj.body
        except:
            logging.info('请求体中无上传文件')
            self.send_error(400)
            return

        short_url = solution.authorLogin + '/' + solution_uuid + '/document/' + filename
        long_url = await nexus_client_async.upload_artifact(short_url, filebody)

        if long_url is None:
            self.send_error(400)
            return

        document = Document()
        document.solutionUuid = solution.uuid
        document.name = filename
        document.url = long_url
        document.fileSize = len(filebody)
        await umm_client_async.create_document(document, token.jwt)

        self.finish()

    async def delete(self, document_id, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            logging.info('JWT验证失败')
            self.send_error(403)
            return

        user_login = token.username
        has_role = token.has_role('ROLE_MANAGER')

        document = Document()
        document.__dict__ = await umm_client_async.get_document(document_id)

        if user_login != document.authorLogin and not has_role:
            self.send_error(403)
            return

        await nexus_client_async.delete_artifact(document.url)
        await umm_client_async.delete_document(document_id, jwt=token.jwt)

        self.finish()
