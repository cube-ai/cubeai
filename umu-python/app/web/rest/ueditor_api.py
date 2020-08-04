import tornado.web
import uuid
from app.service import token_service
from app.service import nexus_client_async
from app.resources import ueditor_config
import logging


class UeditorApi(tornado.web.RequestHandler):

    def get(self, *args, **kwargs):

        action = self.get_argument('action')

        if action == 'config':
            self.write(ueditor_config.config)
        else:
            self.write({'state': '不支持操作！'})

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            logging.info('JWT验证失败')
            self.send_error(403)
            return

        action = self.get_argument('action')
        if action != 'uploadimage' and action != 'uploadscrawl':
            self.write({'state': '不支持操作！'})
            return

        try:
            file_obj = self.request.files.get('upfile')[0]
            filename = file_obj.filename
            filebody = file_obj.body
        except:
            self.write({'state': '文件为空！'})
            return

        name = str(uuid.uuid4()).replace('-', '')
        ext = filename[filename.rfind('.'):]
        filename = name + ext

        short_url = "ueditor/picture/" + filename
        long_url = await nexus_client_async.upload_artifact(short_url, filebody)

        if long_url:
            self.write({
                'state': 'SUCCESS',
                'url': long_url,
            })
        else:
            self.write({'state': '上传文件出错'})
