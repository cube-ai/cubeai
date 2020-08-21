class Self(dict):
    def __init__(self, version, name, description, component_type):
        dict.__init__(self, version=version, name=name, description=description, component_type=component_type)
