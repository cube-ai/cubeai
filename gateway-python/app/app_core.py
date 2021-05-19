# -*- coding: utf-8 -*-
from app.global_data.global_data import g
from app.service import gateway_service, login_service, third_party_oauth_service


class AppCore(object):
    
    def __init__(self):
        g.load_global_data()
        if not g.init_success:
            raise Exception('初始化加载 global_data 失败！')

    def forward_request(self, prev_request):
        return gateway_service.forward_request(prev_request)

    def login(self, **args):
        return login_service.login(**args)

    def logout(self, **args):
        return login_service.logout(**args)

    def login_cmd(self, **args):
        return login_service.login_cmd(**args)

    def third_party_oauth(self, request):
        return third_party_oauth_service.third_party_oauth(request)