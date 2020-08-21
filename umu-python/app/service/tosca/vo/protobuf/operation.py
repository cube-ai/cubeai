class Operation(dict):
    def __init__(self, operationType=None, operationName=None, listOfInputMessages=None, listOfOutputMessages=None):
        dict.__init__(self, operationType=operationType, operationName=operationName
                      , listOfInputMessages=listOfInputMessages, listOfOutputMessages=listOfOutputMessages)

