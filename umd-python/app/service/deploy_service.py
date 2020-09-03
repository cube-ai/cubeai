import time
import uuid
import threading
from app.utils import mytime
from app.utils.file_tools import replace_special_char
from app.service import token_service, umm_client, message_service
from app.service.task_service import save_task_progress, save_task_step_progress, deletes_task_steps
from app.domain.task import Task
from app.domain.solution import Solution
from app.domain.deployment import Deployment
from app.global_data.global_data import g


def deploy_model(**args):
    http_request = args.get('http_request')
    token = token_service.get_token(http_request)
    user_login = token.username
    if user_login is None:
        raise Exception('403 Forbidden')

    res =umm_client.get_solutions(args.get('solutionUuid'), token.jwt)
    if res['status'] != 'ok':
        raise Exception('500: 从umm获取模型对象失败')
    solution = res['value']['results'][0]

    is_public = args.get('public')
    if solution.get('active') and not is_public:
        raise Exception('403 Forbidden: 公开模型不能部署为私有')

    task = Task()
    task.uuid = str(uuid.uuid4()).replace('-', '')
    task.userLogin = user_login
    task.taskName = solution.get('name')
    task.taskType = '模型部署'
    task.taskStatus = '等待调度'
    task.taskProgress = 0
    task.targetUuid = solution.get('uuid')  # 约定部署实例的targetUuid与solution的uuid一致
    task.startDate = mytime.now()

    res = umm_client.create_task(task, jwt=token.jwt)
    if res['status'] != 'ok':
        raise Exception('500: 向umm创建模型部署任务失败')

    task.id = res['value']
    thread = threading.Thread(target=deploy_thread, args=(task, is_public))
    thread.setDaemon(True)
    thread.start()

    return task.uuid


def deploy_thread(task, is_public):
    save_task_progress(task, "正在执行", 5, "启动模型部署...")
    save_task_step_progress(task.uuid, '模型部署', '执行', 5, '启动模型部署...')

    try:
        namespace = 'ucumos-' + replace_special_char(task.userLogin)
    except:
        save_task_step_progress(task.uuid, '模型部署', '失败', 100, '获取用户信息失败。')
        save_task_progress(task, '失败', 100, '获取用户信息失败。', mytime.now())
        return

    if do_deploy(task, namespace, is_public):
        complete = '完成'
    else:
        delete_deploy(task.uuid, namespace)
        complete = '失败'

    message_service.send_message(
        task.userLogin,
        '模型 ' + task.taskName + ' 部署' + complete,
        '你的模型 ' + task.taskName + ' 部署' + complete + '！\n\n请点击下方[目标页面]按钮查看任务详情。',
        '/pmodelhub/#/deploy/view/' + task.uuid,
        False
    )


def do_deploy(task, namespace, is_public):
    save_task_step_progress(task.uuid, '模型部署', '执行', 10, '开始创建namespace...')
    try:
        g.k8s_client.core_api.read_namespace(namespace)
    except:
        body = {
            'apiVersion': 'v1',
            'kind': 'Namespace',
            'metadata': {
                'name': namespace
            }
        }
        try:
            g.k8s_client.core_api.create_namespace(body=body)
        except:
            save_task_step_progress(task.uuid, '模型部署', '失败', 100, '创建namespace失败')
    save_task_step_progress(task.uuid, '模型部署', '执行', 15, '创建namespace完成。')
    save_task_progress(task, '正在执行', 15, '创建namespace成功。')

    if not create_deployment(task, namespace):
        save_task_progress(task, '失败', 100, '创建Kubernetes部署对象失败...', mytime.now())
        return False
    save_task_progress(task, '正在执行', 80, '创建Kubernetes部署对象成功。')

    if not create_service(task, namespace):
        save_task_progress(task, '失败', 100, '创建Kubernetes服务对象失败...', mytime.now())
        return False
    save_task_progress(task, '正在执行', 90, '创建Kubernetes服务对象成功。')

    if not save_deployment(task, namespace, is_public):
        save_task_progress(task, '失败', 100, '保存模型部署对象失败...', mytime.now())
        return False
    save_task_progress(task, '成功', 100, '成功完成模型部署。')

    return True


def create_deployment(task, namespace):
    save_task_step_progress(task.uuid, '模型部署', '执行', 20, '开始提取Docker镜像文件...')
    res = umm_client.get_artifacts(task.targetUuid, 'DOCKER镜像')
    if res['status'] != 'ok':
        save_task_step_progress(task.uuid, '模型部署', '失败', 100, 'Docker镜像不存在...')
        return False

    artifact = res['value'][0]
    image_url = artifact.get('url')
    save_task_step_progress(task.uuid, '模型部署', '执行', 30, '提取Docker镜像文件完成。')

    save_task_step_progress(task.uuid, '模型部署', '执行', 35, '开始创建Kubernetes部署对象...')
    body = {
        'apiVersion': 'apps/v1',
        'kind': 'Deployment',
        'metadata': {
            'namespace': namespace,
            'name': 'deployment-' + task.uuid
        },
        'spec': {
            'replicas': 1,
            'selector': {
                'matchLabels': {
                    'ucumos': task.uuid
                }
            },
            'template': {
                'metadata': {
                    'labels': {
                        'ucumos': task.uuid
                    }
                },
                'spec': {
                    'containers': [{
                        'name': task.targetUuid,
                        'image': image_url,
                        'port': {
                            'containerPort': 3330
                        },
                        'resources': {
                            'requests': {
                                'memory': '1Gi',
                                'cpu': '0.5'
                            },
                            'limits': {
                                'memory': '8Gi',
                                'cpu': '2'
                            }
                        }
                    }]
                }
            }
        }
    }
    try:
        g.k8s_client.apps_api.create_namespaced_deployment(namespace=namespace, body=body)
    except:
        save_task_step_progress(task.uuid, '模型部署', '失败', 100, '创建Kubernetes部署对象失败。')
        return False

    error_count = 0
    ok_count = 0
    ok = False
    progress = 40
    for i in range(1800):
        try:
            res = g.k8s_client.apps_api.read_namespaced_deployment_status("deployment-" + task.uuid, namespace)
            available = res.status.available_replicas
            ready = res.status.ready_replicas
            replicas = res.status.replicas
            if available and ready and replicas and available > 0 and ready > 0 and available == replicas and ready == replicas:
                ok_count += 1
                if ok_count > 15:
                    ok = True
                    break
            elif ok_count > 0:
                save_task_step_progress(task.uuid, '模型部署', '失败', 100, '模型程序运行出错。')
                ok = False
                break
        except:
            error_count += 1
            if error_count > 30:
                save_task_step_progress(task.uuid, '模型部署', '失败', 100, '获取Kubernetes部署对象状态失败。')
                ok = False
                break

        save_task_step_progress(task.uuid, '模型部署', '执行', progress, '正在创建Kubernetes部署对象...')
        progress += 1
        if progress > 79:
            progress = 40
        time.sleep(1)

    deletes_task_steps(task.uuid, 40, 80)

    if ok:
        save_task_step_progress(task.uuid, '模型部署', '执行', 80, '创建Kubernetes部署对象完成。')
    else:
        save_task_step_progress(task.uuid, '模型部署', '失败', 100, '创建Kubernetes部署对象超时。')

    return ok


def create_service(task, namespace):
    save_task_step_progress(task.uuid, '模型部署', '执行', 85, '开始创建Kubernetes服务对象...')
    body = {
        'apiVersion': 'v1',
        'kind': 'Service',
        'metadata': {
            'namespace': namespace,
            'name': 'service-' + task.uuid
        },
        'spec': {
            'type': 'NodePort',
            'selector': {
                'ucumos': task.uuid
            },
            'ports': [{
                'port': 3330,
                'targetPort': 3330
            }],
        }
    }
    try:
        g.k8s_client.core_api.create_namespaced_service(namespace=namespace, body=body)
    except:
        save_task_step_progress(task.uuid, '模型部署', '失败', 100, '创建Kubernetes服务对象失败。')
        return False
    save_task_step_progress(task.uuid, '模型部署', '执行', 90, '创建Kubernetes服务对象完成。')

    return True


def save_deployment(task, namespace, is_public):
    save_task_step_progress(task.uuid, '模型部署', '执行', 92, '开始保存模型部署对象...')
    try:
        k8s_status = g.k8s_client.core_api.read_namespaced_service_status("service-" + task.uuid, namespace)
    except:
        save_task_step_progress(task.uuid, '模型部署', '失败', 100, '获取Kubernetes服务访问端口失败。')
        return False

    res = umm_client.get_solutions(task.targetUuid)
    if res['status'] != 'ok':
        save_task_step_progress(task.uuid, '模型部署', '失败', 100, '从umm获取模型对象失败。')
        return False
    solution = Solution()
    solution.__dict__ = res['value']['results'][0]
    deployment = Deployment()
    deployment.uuid = task.uuid
    deployment.deployer = task.userLogin
    deployment.solutionUuid = task.targetUuid
    deployment.solutionName = task.taskName
    deployment.solutionAuthor = solution.authorLogin
    deployment.pictureUrl = solution.pictureUrl
    deployment.k8sPort = k8s_status.spec.ports[0].node_port
    deployment.isPublic = is_public
    deployment.status = '运行'

    res = umm_client.create_deployment(deployment, jwt=g.oauth_client.get_jwt())
    if res['status'] != 'ok':
        save_task_step_progress(task.uuid, '模型部署', '失败', 100, '保存模型部署对象失败。')

    save_task_step_progress(task.uuid, '模型部署', '执行', 98, '保存模型部署对象完成。')
    save_task_step_progress(task.uuid, '模型部署', '成功', 100, '模型部署成功。')
    return True


def delete_deploy(name, namespace):
    try:
        g.k8s_client.apps_api.delete_namespaced_deployment("deployment-" + name, namespace)
        g.k8s_client.core_api.delete_namespaced_service("service-" + name, namespace)
    except:
        pass
