from app.utils import mytime
from app.utils.file_tools import replace_special_char
from app.service import umm_client
from app.service.task_service import save_task_progress, save_task_step_progress
from app.domain.task import Task
from app.globals.globals import g


def stop(task_uuid, deployment):
    tasks = umm_client.get_tasks(task_uuid, jwt=g.oauth_client.get_jwt())
    if tasks is None:
        return

    task = Task()
    task.__dict__= tasks[0]

    save_task_progress(task, "正在执行", 10, "准备停止模型实例运行...")
    save_task_step_progress(task.uuid, '实例停止', '执行', 10, '准备停止模型实例运行...')

    try:
        namespace = 'ucumos-' + replace_special_char(task.userLogin)
    except:
        save_task_step_progress(task.uuid, '模型部署', '失败', 100, '构造用户namespace信息失败。')
        save_task_progress(task, '失败', 100, '构造用户namespace信息失败。', mytime.now())
        return

    save_task_progress(task, "正在执行", 30, "访问Kubernetes命名空间...")
    save_task_step_progress(task.uuid, '实例停止', '执行', 30, '访问Kubernetes命名空间...')
    try:
        g.k8s_client.core_api.read_namespace(namespace)
    except:
        save_task_progress(task, "失败", 100, "访问Kubernetes命名空间失败...")
        save_task_step_progress(task.uuid, '实例停止', '失败', 100, '访问Kubernetes命名空间失败...')
        return

    save_task_progress(task, "正在执行", 50, "停止运行模型实例...")
    save_task_step_progress(task.uuid, '实例停止', '执行', 50, '停止运行模型实例...')
    try:
        g.k8s_client.apps_api.delete_namespaced_deployment("deployment-" + deployment.uuid, namespace)
        g.k8s_client.core_api.delete_namespaced_service("service-" + deployment.uuid, namespace)
    except Exception as e:
        pass

    umm_client.update_deployment_status({
        'id': deployment.id,
        'status': '停止'
    }, g.oauth_client.get_jwt())

    save_task_progress(task, "成功", 100, "成功停止运行模型实例。")
    save_task_step_progress(task.uuid, '成功', '执行', 100, '成功停止运行模型实例。')

