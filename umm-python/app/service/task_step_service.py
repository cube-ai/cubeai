from app.domain.task_step import TaskStep
from app.service import token_service
from app.database import task_step_db
from app.utils import mytime


def create_task_step(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')

    user_login = token.username
    # 只能由umu微服务中的异步任务OnBoardingServie调用，不能由前端用户调用
    if user_login != 'internal':
        raise Exception('403 Forbidden')

    task_step = TaskStep()
    task_step.__dict__ = args.get('task_step')
    task_step.stepDate = mytime.now()

    id = task_step_db.create_task_step(task_step)
    return id


def get_task_steps(**args):
    id = args.get('lastId')
    taskUuid = args.get('taskUuid')
    stepName = args.get('stepName')

    where = 'WHERE id > {} and task_uuid = "{}" and step_name = "{}"'.format(id, taskUuid, stepName)
    results = task_step_db.get_task_steps(where)

    return results


def deletes_task_steps(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')

    user_login = token.username
    # 只能由umu微服务中的异步任务OnBoardingServie调用，不能由前端用户调用
    if user_login != 'internal':
        raise Exception('403 Forbidden')

    task_step_db.delete_task_steps(args.get('task_uuid'), args.get('start_progress'), args.get('end_progress'))
    return 0
