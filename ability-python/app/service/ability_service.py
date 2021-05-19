import requests
from app.service import umm_client
from app.global_data.global_data import g


def forward_request(prev_request):
    path = prev_request.path

    if path.startswith('/model/'):
        deployment_uuid = path[7:]

        res = umm_client.find_ability(deployment_uuid)
        if res['status'] != 'ok':
            raise Exception('未找到部署实例')

        deployment = res['value']

        k8s_port = deployment.get('k8sPort')
        if k8s_port is None:
            raise Exception('k8s实例端口停止服务')

        internal_ip = g.get_central_config()['kubernetes']['ability']['internalIP']
        url = 'http://{}:{}/api/model'.format(internal_ip, k8s_port)

        res = requests.post(url=url, data=prev_request.body, headers=prev_request.headers)

        return {
            'response': res,
        }

    if path.startswith('/file/'):
        deployment_uuid, method = path[6:].split('/')

        res = umm_client.find_ability(deployment_uuid)
        if res['status'] != 'ok':
            raise Exception('未找到部署实例')

        deployment = res['value']

        k8s_port = deployment.get('k8sPort')
        if k8s_port is None:
            raise Exception('k8s实例端口停止服务')

        internal_ip = g.get_central_config()['kubernetes']['ability']['internalIP']
        url = 'http://{}:{}/api/file/{}'.format(internal_ip, k8s_port, method)

        res = requests.post(url=url, data=prev_request.body, headers=prev_request.headers)

        return {
            'response': res,
        }

    if path.startswith('/stream/'):
        deployment_uuid, method = path[8:].split('/')

        res = umm_client.find_ability(deployment_uuid)
        if res['status'] != 'ok':
            raise Exception('未找到部署实例')

        deployment = res['value']

        k8s_port = deployment.get('k8sPort')
        if k8s_port is None:
            raise Exception('k8s实例端口停止服务')

        internal_ip = g.get_central_config()['kubernetes']['ability']['internalIP']
        url = 'http://{}:{}/api/stream/{}'.format(internal_ip, k8s_port, method)

        res = requests.post(url=url, data=prev_request.body, headers=prev_request.headers)

        return {
            'response': res,
        }

    if path.startswith('/web/'):
        path = path[5:]
        i = path.find('/')
        deployment_uuid = path[:i]
        filename = path[i+1:]

        res = umm_client.find_ability(deployment_uuid)
        if res['status'] != 'ok':
            raise Exception('未找到部署实例')
        deployment = res['value']

        k8s_port = deployment.get('k8sPort')
        if k8s_port is None:
            raise Exception('k8s实例端口停止服务')

        internal_ip = g.get_central_config()['kubernetes']['ability']['internalIP']
        url = 'http://{}:{}/web/{}'.format(internal_ip, k8s_port, filename)

        res = requests.get(url=url, headers=prev_request.headers)

        return {
            'response': res,
        }

    raise Exception('unsupported API name')
