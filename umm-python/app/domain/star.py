class Star:
    def __init__(self):
        self.id = None
        self.userLogin = None
        self.targetType = None
        self.targetUuid = None
        self.starDate = None

    def from_record(self, record):
        self.id = record[0]
        self.userLogin = record[1]
        self.targetType = record[2]
        self.targetUuid = record[3]
        self.starDate = record[4].strftime('%Y-%m-%dT%H:%M:%S')
