from app.utils.file_tools import replace_special_char
from app.service import token_service, umm_client
from app.domain.deployment import Deployment
from app.domain.deployment_status import DeploymentStatus
from app.global_data.global_data import g


def get_deployment_status(**args):
    username = args.get('username')
    deployment_uuid = args.get('deploymentUuid')
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_OPERATOR')
    user_login = token.username
    if user_login is None or (user_login != username and not has_role):
        raise Exception('403 Forbidden')

    try:
        namespace = 'ucumos-' + replace_special_char(username)
        res = g.k8s_client.apps_api.read_namespaced_deployment_status('deployment-' + deployment_uuid, namespace)

        deployment_status = DeploymentStatus()
        if res.status.replicas:
            deployment_status.replicas = res.status.replicas
        if res.status.ready_replicas:
            deployment_status.replicasReady = res.status.ready_replicas
        deployment_status.limitsCpu = res.spec.template.spec.containers[0].resources.limits["cpu"]
        deployment_status.limitsMem = res.spec.template.spec.containers[0].resources.limits["memory"]
        deployment_status.requestsCpu = res.spec.template.spec.containers[0].resources.requests["cpu"]
        deployment_status.requestsMem = res.spec.template.spec.containers[0].resources.requests["memory"]
    except:
        raise Exception('获取k8s状态失败')

    return deployment_status.__dict__


def get_deployment_logs(**args):
    username = args.get('username')
    deployment_uuid = args.get('deploymentUuid')
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_OPERATOR')
    user_login = token.username
    if user_login is None or (user_login != username and not has_role):
        raise Exception('403 Forbidden')

    try:
        namespace = 'ucumos-' + replace_special_char(username)
        res = g.k8s_client.core_api.list_namespaced_pod(namespace, label_selector='ucumos=' + deployment_uuid)

        lines = []
        for pods in res.items:
            if pods.status.phase != 'Running':
                continue
            lines.append('[pod ' + pods.metadata.name[-15:] + ']:')
            lines.append(g.k8s_client.core_api.read_namespaced_pod_log(pods.metadata.name, namespace))
    except:
        raise Exception('获取k8s日志失败')

    return '\n'.join(lines)


def scale_deployment(**args):
    deployment = Deployment()
    deployment.__dict__ = args.get('deployment')
    target_status = DeploymentStatus()
    target_status.__dict__ = args.get('targetStatus')
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_OPERATOR')
    user_login = token.username
    if user_login is None or (user_login != deployment.deployer and not has_role):
        raise Exception('403 Forbidden')

    try:
        name = 'deployment-' + deployment.uuid
        namespace = 'ucumos-' + replace_special_char(deployment.deployer)

        old_status = g.k8s_client.apps_api.read_namespaced_deployment_status(name, namespace)
        body = {
            'spec': {
                'replicas': target_status.replicas,
                'template': {
                    'spec': {
                        'containers': [
                            {
                                "name": old_status.spec.template.spec.containers[0].name,
                                "resources": {
                                    "requests": {
                                        "cpu": target_status.requestsCpu,
                                        "memory": target_status.requestsMem
                                    },
                                    "limits": {
                                        "cpu": target_status.limitsCpu,
                                        "memory": target_status.limitsMem
                                    }
                                }
                            }
                        ]
                    }
                }
            }
        }
        g.k8s_client.apps_api.patch_namespaced_deployment(name, namespace, body)
    except:
        raise Exception('k8s扩缩容失败')

    return 0


def pause_deployment(**args):
    deployment = Deployment()
    deployment.__dict__ = args.get('deployment')
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_OPERATOR')
    user_login = token.username
    if user_login is None or (user_login != deployment.deployer and not has_role):
        raise Exception('403 Forbidden')

    try:
        name = 'deployment-' + deployment.uuid
        namespace = 'ucumos-' + replace_special_char(deployment.deployer)

        body = {'spec': {'replicas': 0}}
        g.k8s_client.apps_api.patch_namespaced_deployment(name, namespace, body)
        umm_client.update_deployment_status(deployment.id, '暂停', g.oauth_client.get_jwt())
    except:
        raise Exception('暂停k8s实体失败')

    return 0


def restart_deployment(**args):
    deployment = Deployment()
    deployment.__dict__ = args.get('deployment')
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_OPERATOR')
    user_login = token.username
    if user_login is None or (user_login != deployment.deployer and not has_role):
        raise Exception('403 Forbidden')

    try:
        name = 'deployment-' + deployment.uuid
        namespace = 'ucumos-' + replace_special_char(deployment.deployer)

        body = {'spec': {'replicas': 1}}
        g.k8s_client.apps_api.patch_namespaced_deployment(name, namespace, body)
        umm_client.update_deployment_status(deployment.id, '运行', g.oauth_client.get_jwt())
    except:
        raise Exception('重启k8s实体失败')

    return 0


def stop_deployment(**args):
    deployment = Deployment()
    deployment.__dict__ = args.get('deployment')
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_OPERATOR')
    user_login = token.username
    if user_login is None or (user_login != deployment.deployer and not has_role):
        raise Exception('403 Forbidden')

    try:
        name = 'deployment-' + deployment.uuid
        service = 'service-' + deployment.uuid
        namespace = 'ucumos-' + replace_special_char(deployment.deployer)

        g.k8s_client.apps_api.delete_namespaced_deployment(name, namespace)
        g.k8s_client.core_api.delete_namespaced_service(service, namespace)

        umm_client.update_deployment_status(deployment.id, '停止', g.oauth_client.get_jwt())
    except:
        raise Exception('停止k8s实体失败')

    return 0
