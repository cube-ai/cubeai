class VerifyCode:
    def __init__(self):
        self.id = None
        self.code = None
        self.expire = None

    def from_record(self, record):
        self.id = record[0]
        self.code = record[1]
        self.expire = record[2].strftime('%Y-%m-%dT%H:%M:%S')
