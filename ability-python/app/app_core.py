# -*- coding: utf-8 -*-
from app.global_data.global_data import g
from app.service import ability_service


class AppCore(object):
    
    def __init__(self):
        g.load_global_data()
        if not g.init_success:
            raise Exception('初始化加载 global_data 失败！')

    def forward_request(self, prev_request):
        return ability_service.forward_request(prev_request)

    def get_web_file(self, prev_request):
        return ability_service.get_web_file(prev_request)

