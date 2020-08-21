class Parameter(dict):
    def __init__(self, name, value, description):
        dict.__init__(self, name=name, value=value, description=description)
