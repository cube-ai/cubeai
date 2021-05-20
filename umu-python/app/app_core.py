# -*- coding: utf-8 -*-
from app.global_data.global_data import g
from app.service import onboarding_service, document_service


class AppCore(object):
    
    # 声明对外公开可通过API接口访问的方法。如public_actions未声明或值为None，则默认本class中定义的所有方法都对外公开。
    public_actions = None

    def __init__(self):
        g.load_global_data()
        if not g.init_success:
            raise Exception('初始化加载 global_data 失败！')

    def hello(self, **args):
        return 'Hello world!'

    def onboard_model(self, **args):
        return onboarding_service.onboard_model(**args)

    def upload_document(self, **args):
        return document_service.upload_document(**args)

    def delete_document(self, **args):
        return document_service.delete_document(**args)

    def download_document(self, **args):
        return document_service.download_document(**args)
