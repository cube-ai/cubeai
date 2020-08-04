import json
import uuid
import threading
import tornado.web
from app.service import token_service
from app.domain.task import Task
from app.domain.deployment import Deployment
from app.service import umm_client
from app.service import lcm_service
from app.utils import mytime


class LcmApi(tornado.web.RequestHandler):

    def put(self, action, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        has_role = token.has_role('ROLE_OPERATOR')

        deployment = Deployment()
        deployment.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))

        if user_login is None or (not has_role and user_login != deployment.deployer):
            self.send_error(403)
            return

        task = Task()
        task.uuid = str(uuid.uuid4()).replace('-', '')
        task.userLogin = user_login
        task.taskStatus = '等待调度'
        task.taskProgress = 0
        task.targetUuid = deployment.uuid
        task.startDate = mytime.now()

        if action == 'stop':
            task.taskType = '实例停止'
            task.taskName = '实例停止-' + deployment.solutionName
            action_service = lcm_service.stop
        else:
            self.send_error(400)
            return

        try:
            umm_client.create_task(task, jwt=token.jwt)
        except:
            self.send_error(500)

        thread = threading.Thread(target=action_service, args=(task.uuid, deployment))
        thread.setDaemon(True)
        thread.start()

        self.finish()
