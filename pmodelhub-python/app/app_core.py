# -*- coding: utf-8 -*-
from app.global_data.global_data import g


class AppCore(object):
    
    # 声明对外公开可通过API接口访问的方法。前端应用默认不开放任何API访问。
    public_actions = ()

    def __init__(self):
        g.load_global_data()
        if not g.init_success:
            raise Exception('初始化加载 global_data 失败！')

    def hello(self, **args):
        return 'Hello world!'

