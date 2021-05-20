class Task:
    def __init__(self):
        self.id = None
        self.uuid = None
        self.taskName = None
        self.taskType = None
        self.taskStatus = None
        self.taskProgress = None
        self.description = None
        self.targetUuid = None
        self.userLogin = None
        self.startDate = None
        self.endDate = None

    def from_record(self, record):
        self.id = record[0]
        self.uuid = record[1]
        self.taskName = record[2]
        self.taskType = record[3]
        self.taskStatus = record[4]
        self.taskProgress = record[5]
        self.description = record[6]
        self.targetUuid = record[7]
        self.userLogin = record[8]
        self.startDate = record[9].strftime('%Y-%m-%dT%H:%M:%S')
        self.endDate = record[10].strftime('%Y-%m-%dT%H:%M:%S')
