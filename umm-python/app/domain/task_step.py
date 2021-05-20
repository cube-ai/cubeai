class TaskStep:
    def __init__(self):
        self.id = None
        self.taskUuid = None
        self.stepName = None
        self.stepStatus = None
        self.stepProgress = None
        self.description = None
        self.stepDate = None

    def from_record(self, record):
        self.id = record[0]
        self.taskUuid = record[1]
        self.stepName = record[2]
        self.stepStatus = record[3]
        self.stepProgress = record[4]
        self.description = record[5]
        self.stepDate = record[6].strftime('%Y-%m-%dT%H:%M:%S')

