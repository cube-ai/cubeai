class Stream(dict):
    def __init__(self, subscribes, publishes):
        dict.__init__(self, subscribes=subscribes, publishes=publishes)
