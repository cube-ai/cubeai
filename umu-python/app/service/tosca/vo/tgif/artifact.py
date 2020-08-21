class Artifact(dict):
    def __init__(self, uri, a_type):
        dict.__init__(self, uri=uri, type=a_type)
