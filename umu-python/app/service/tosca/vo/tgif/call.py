class Call(dict):
    def __init__(self, config_key, request, response):
        dict.__init__(self, config_key=config_key, request=request, response=response)


