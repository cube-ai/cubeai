import json
import yaml
import random
import consul
import requests
import logging


class ConsulClient:
    def __init__(self, host=None, port=None, token=None):
        self.host = host
        self.port = port
        self.token = token
        self.consul = consul.Consul(host=host, port=port)
        self.registered = False

    def register(self, name, service_id, address, port, tags, http_check_url):
        try:
            self.consul.agent.service.register(
                name,
                service_id=service_id,
                address=address,
                port=port,
                tags=tags,
                check=consul.Check().http(http_check_url, '10s', '20s', '20s')
            )
            self.registered = True
            logging.critical('Successful registered to Consul: {}:{}'.format(address, port))
        except:
            self.registered = False

    def get_services(self):
        return self.consul.agent.services()

    def resolve_service(self, name):  # 根据服务名解析其“IP地址:端口号”，负载均衡随机取其中之一
        url = 'http://{}:{}/v1/catalog/service/{}'.format(self.host, self.port, name)
        res = requests.get(url)
        if res.status_code != 200:
            return None

        instances = json.loads(res.text)

        dc_list = []
        for instance in instances:
            dc_list.append(instance.get('Datacenter'))

        results = []
        for dc in dc_list:
            url = 'http://{}:{}/v1/health/service/{}?dc={}&token={}'.format(self.host,self.port, name, dc, self.token if self.token else '')
            res = requests.get(url)
            if res.status_code != 200:
                return None

            text = res.text
            instances = json.loads(text)

            for instance in instances:
                status = instance.get('Checks')[0].get('Status')
                if status == 'passing':
                    address = instance.get('Service').get('Address')
                    port = instance.get('Service').get('Port')
                    results.append({'port': port, 'address': address})

        if len(results) < 1:
            return None
        else:
            result = results[random.randint(0, len(results) - 1)]  # 随机获取一个可用的服务实例
            return '{}:{}'.format(result['address'], result['port'])

    def get_kv(self):
        try:
            kv_yaml = self.consul.kv.get('config/application/data')[1].get('Value')
            return yaml.load(kv_yaml, Loader=yaml.SafeLoader)
        except:
            return None


if __name__ == "__main__":
    consul_client = ConsulClient('127.0.0.1', 8500)
    consul_client.register('demo',  'demo-test', '127.0.0.1', 8888, ['profile-dev'], 'http://127.0.0.1:8888/management/health')
    if consul_client.registered:
        print('Registered in Consul!')
        print(consul_client.resolve_service('uaa'))
        print(consul_client.resolve_service('demo'))
        print(consul_client.get_kv())
    else:
        print('Not registered in Consul!!!')

