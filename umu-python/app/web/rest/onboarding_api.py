import tornado.web
import threading
from app.service import token_service
from app.service import umm_client_async
from app.service import onboarding_service
from app.utils.file_tools import *
from app.domain.task import Task
import logging


class OnboardingApi(tornado.web.RequestHandler):

    async def post(self, task_uuid, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            logging.info('JWT验证失败')
            self.send_error(403)
            return

        user_login = token.username

        try:
            file_obj = self.request.files.get(user_login)[0]
            filename = file_obj.filename
            filebody = file_obj.body
        except:
            logging.info('请求体中无上传文件')
            self.send_error(400)
            return

        user_home = os.path.expanduser('~')
        base_path = user_home + '/tempfile/ucumosmodels/' + user_login + '/' + task_uuid
        make_path(base_path)
        del_path_files(base_path)

        file_path = os.path.join(base_path, filename)
        with open(file_path, 'wb') as f:
            f.write(filebody)

        task = Task()
        task.uuid = task_uuid
        task.userLogin = user_login
        task.taskName = filename
        task.taskType = '模型导入'
        task.taskStatus = '等待调度'
        task.taskProgress = 0
        task.targetUuid = task_uuid

        try:
            await umm_client_async.create_task(task, jwt=token.jwt)
        except:
            self.send_error(500)

        thread = threading.Thread(target=onboarding_service.onboarding, args=(task_uuid,))
        thread.setDaemon(True)
        thread.start()

        self.finish()
