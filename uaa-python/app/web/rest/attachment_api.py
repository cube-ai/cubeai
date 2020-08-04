import json
import uuid
import logging
import tornado.web
from app.domain.attachment import Attachment
from app.service import token_service
from app.service import nexus_client_async
from app.database import attachment_db
from app.utils import mytime


class AttachmentGetApi(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        has_role = token.has_role('ROLE_CONTENT')
        if not has_role:
            self.send_error(403)
            return

        pageable = {
            'page': self.get_argument('page', None),
            'size': self.get_argument('size', None),
            'sort': self.get_arguments('sort'),
        }

        total_count, result = await attachment_db.get_attachments('', pageable)
        self.set_header('X-Total-Count', total_count)

        self.write(json.dumps(result))


class AttachmentUploadApi(tornado.web.RequestHandler):

   async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        has_role = token.has_role('ROLE_CONTENT')
        if not has_role:
            self.send_error(403)
            return

        try:
            file_obj = self.request.files.get('attachment')[0]
            filename = file_obj.filename
            filebody = file_obj.body
        except:
            logging.info('请求体中无上传文件')
            self.send_error(400)
            return

        short_url = 'attachment/{}/{}'.format(str(uuid.uuid4()).replace('-', ''), filename)
        long_url = await nexus_client_async.upload_artifact(short_url, filebody)

        if long_url is None:
            self.send_error(400)
            return

        attachment = Attachment()
        attachment.authorLogin = user_login
        attachment.name = filename
        attachment.url = long_url
        attachment.fileSize = len(filebody)
        attachment.createdDate = mytime.now()
        attachment.modifiedDate = mytime.now()
        await attachment_db.create_attachment(attachment)


class AttachmentDelApi(tornado.web.RequestHandler):

    async def delete(self, id,  *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        has_role = token.has_role('ROLE_ADMIN')

        attachment = await attachment_db.get_attachment(id)

        if not has_role and attachment.authorLogin != user_login:
            self.send_error(403)
            return

        await nexus_client_async.delete_artifact(attachment.url)
        await attachment_db.delete_attachment(id)
