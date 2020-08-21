class Response(dict):
    def __init__(self, r_format, version):
        dict.__init__(self, format=r_format, version=version)
