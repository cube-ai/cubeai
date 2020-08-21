class Provide(dict):
    def __init__(self, route, request, response):
        dict.__init__(self, route=route, request=request, response=response)
