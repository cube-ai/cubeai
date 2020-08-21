class Service(dict):
    def __init__(self, calls, provides):
        dict.__init__(self, calls=calls, provides=provides)
