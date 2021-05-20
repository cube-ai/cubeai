class QqUser:
    def __init__(self):
        self.id = None
        self.qqLogin = None
        self.userLogin = None

    def from_record(self, record):
        self.id = record[0]
        self.qqLogin = record[1]
        self.userLogin = record[2]
