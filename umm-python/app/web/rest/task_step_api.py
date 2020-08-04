import tornado.web
from app.domain.task_step import TaskStep
from app.service import token_service
from app.database import task_step_db
from app.utils import mytime
import json


class TaskStepApi(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        user_login = token.username
        if user_login != 'internal':
            self.send_error(403)
            return

        task_step = TaskStep()
        task_step.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))
        task_step.stepDate = mytime.now()

        await task_step_db.create_task_step(task_step)
        self.set_status(201)
        self.finish()

    async def get(self, *args, **kwargs):
        id = self.get_argument('id', None)
        taskUuid = self.get_argument('taskUuid', None)
        stepName = self.get_argument('stepName', None)

        where = 'WHERE id > {} and task_uuid = "{}" and step_name = "{}"'.format(id, taskUuid, stepName)
        result = await task_step_db.get_task_steps(where)

        self.write(json.dumps(result))
