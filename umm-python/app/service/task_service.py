from app.domain.task import Task
from app.service import token_service
from app.database import task_db
from app.utils import mytime


def create_task(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')

    task = Task()
    task.__dict__ = args.get('task')
    task.description = ''
    task.startDate = mytime.now()
    task.endDate = mytime.now()

    user_login = token.username
    if user_login != task.userLogin:
        raise Exception('403 Forbidden')

    id = task_db.create_task(task)
    return id


def update_task(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')

    user_login = token.username
    # updateTask只能由umu微服务中的异步任务OnBoardingServie调用，不能由前端用户调用
    if user_login != 'internal':
        raise Exception('403 Forbidden')

    task = Task()
    task.__dict__ = args.get('task')
    task.endDate = mytime.now()

    task_db.update_task(task)
    return 0


def get_tasks(**args):
    uuid = args.get('uuid')
    userLogin = args.get('userLogin')
    taskStatus = args.get('taskStatus')
    pageable = {
        'page': args.get('page'),
        'size': args.get('size'),
        'sort': args.get('sort'),
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
    total, results = task_db.get_tasks(where, pageable)

    return {
        'total': total,
        'results': results,
    }


def find_task(**args):
    result = task_db.get_task(args.get('taskId'))
    return result


def delete_task(**args):
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_ADMIN')
    if not has_role:
        raise Exception('403 Forbidden')

    task_db.delete_task(args.get('taskId'))
    return 0
