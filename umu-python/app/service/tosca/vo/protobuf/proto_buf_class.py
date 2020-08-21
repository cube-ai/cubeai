class ProtoBufClass(dict):
    def __init__(self, syntax=None, packageName=None, listOfOption=None, listOfMessages=None, service=None):
        dict.__init__(self, syntax=syntax, packageName=packageName, listOfOption=listOfOption,
                      listOfMessages=listOfMessages, service=service)

