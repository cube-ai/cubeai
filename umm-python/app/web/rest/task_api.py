import tornado.web
from app.domain.task import Task
from app.service import token_service
from app.database import task_db
from app.utils import mytime
import json


class TaskApiA(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        task = Task()
        task.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))
        task.description = ''
        task.startDate = mytime.now()
        task.endDate = mytime.now()

        user_login = token.username
        if user_login != task.userLogin:
            self.send_error(403)
            return

        await task_db.create_task(task)
        self.set_status(201)
        self.finish()

    async def put(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        user_login = token.username
        # updateTask只能由umu微服务中的异步任务OnBoardingServie调用，不能由前端用户调用
        if user_login != 'internal':
            self.send_error(403)
            return

        task = Task()
        task.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))
        task.endDate = mytime.now()

        await task_db.update_task(task)
        self.set_status(201)
        self.finish()

    async def get(self, *args, **kwargs):
        uuid = self.get_argument('uuid', None)
        userLogin = self.get_argument('userLogin', None)
        taskStatus = self.get_argument('taskStatus', None)

        pageable = {
            'page': self.get_argument('page', None),
            'size': self.get_argument('size', None),
            'sort': self.get_arguments('sort'),
        }

        where = ''
        if uuid is not None:
            where += 'and uuid = "{}" '.format(uuid)
        if userLogin is not None:
            where += 'and user_login = "{}" '.format(userLogin)
        if taskStatus is not None:
            where += 'and task_status = "{}" '.format(taskStatus)
        where = where[4:]

        if where != '':
            where = 'WHERE ' + where
        total_count, result = await task_db.get_tasks(where, pageable)
        self.set_header('X-Total-Count', total_count)

        self.write(json.dumps(result))


class TaskApiB(tornado.web.RequestHandler):

    async def get(self, id, *args, **kwargs):
        result = await task_db.get_task(id)
        self.write(result)

    async def delete(self, id, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        has_role = token.has_role('ROLE_ADMIN')
        if not has_role:
            self.send_error(403)
            return

        await task_db.delete_task(id)
        self.set_status(200)
        self.finish()
