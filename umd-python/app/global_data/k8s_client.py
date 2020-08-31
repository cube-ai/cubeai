from kubernetes import client


class K8sClient:
    def __init__(self, host, token, ipIpTunnelCidr):
        self.host = host
        self.token = token
        self.ipIpTunnelCidr = ipIpTunnelCidr
        self.config = None
        self.api_client = None
        self.core_api = None
        self.apps_api = None

    def init_v1api(self):
        self.config = client.Configuration()
        self.config.host = self.host
        self.config.verify_ssl = False
        self.config.api_key = {"authorization": "Bearer " + self.token}
        self.api_client = client.ApiClient(self.config)
        self.core_api = client.CoreV1Api(self.api_client)
        self.apps_api = client.AppsV1Api(self.api_client)
