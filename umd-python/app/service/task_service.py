from app.domain.task_step import TaskStep
from app.service import umm_client
from app.utils import mytime
from app.globals.globals import g


def save_task_progress(task, taskStatus, taskProgress, description, endDate=None):
    task.taskStatus = taskStatus
    task.taskProgress = taskProgress
    task.description = description
    if endDate is not None:
        task.endDate = endDate
    umm_client.update_task(task, jwt=g.oauth_client.get_jwt())


def save_task_step_progress(taskUuid, stepName, stepStatus, stepProgress, description):
    task_step = TaskStep()
    task_step.taskUuid = taskUuid
    task_step.stepName = stepName
    task_step.stepStatus = stepStatus
    task_step.stepProgress = stepProgress
    task_step.description = description
    task_step.stepDate = mytime.now()

    umm_client.create_task_step(task_step, jwt=g.oauth_client.get_jwt())
