# -*- coding: utf-8 -*-
from app.global_data.global_data import g
from app.service import deploy_service, lcm_service


class AppCore(object):
    
    # 声明对外公开可通过API接口访问的方法。如public_actions未声明或值为None，则默认本class中定义的所有方法都对外公开。
    public_actions = None

    def __init__(self):
        g.load_global_data()
        if not g.init_success:
            raise Exception('初始化加载 global_data 失败！')

    def hello(self, **args):
        return 'Hello world!'

    def deploy_model(self, **args):
        return deploy_service.deploy_model(**args)

    def get_deployment_status(self, **args):
        return lcm_service.get_deployment_status(**args)

    def get_deployment_logs(self, **args):
        return lcm_service.get_deployment_logs(**args)

    def scale_deployment(self, **args):
        return lcm_service.scale_deployment(**args)

    def pause_deployment(self, **args):
        return lcm_service.pause_deployment(**args)

    def restart_deployment(self, **args):
        return lcm_service.restart_deployment(**args)

    def stop_deployment(self, **args):
        return lcm_service.stop_deployment(**args)
