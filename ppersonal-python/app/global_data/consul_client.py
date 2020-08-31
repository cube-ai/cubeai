import consul
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
