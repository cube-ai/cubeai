import json
import threading
import tornado.web
from app.service import token_service
from app.domain.task import Task
from app.service import umm_client_async
from app.service import deploy_service
from app.utils import mytime


class DeployApi(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        if user_login is None:
            self.send_error(403)
            return

        body = json.loads(str(self.request.body, encoding='utf-8'))
        solutions = await umm_client_async.get_solutions(body.get('solutionUuid'), token.jwt)
        solution = solutions[0]

        is_public = body.get('public')
        if solution.get('active') and not is_public:
            self.send_error(403)  # 公开模型不能部署为私有
            return

        task = Task()
        task.uuid = body.get('taskUuid')
        task.userLogin = user_login
        task.taskName = solution.get('name')
        task.taskType = '模型部署'
        task.taskStatus = '等待调度'
        task.taskProgress = 0
        task.targetUuid = solution.get('uuid')  # 约定部署实例的targetUuid与solution的uuid一致
        task.startDate = mytime.now()

        try:
            await umm_client_async.create_task(task, jwt=token.jwt)
        except:
            self.send_error(500)

        thread = threading.Thread(target=deploy_service.deploy, args=(task.uuid, is_public))
        thread.setDaemon(True)
        thread.start()

        self.finish()
