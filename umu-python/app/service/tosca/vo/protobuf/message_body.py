class MessageBody(dict):
    def __init__(self, messageName=None, messageargumentList=None):
        dict.__init__(self, messageName=messageName, messageargumentList=messageargumentList)
