class Comment:
    def __init__(self):
        self.id = None
        self.uuid = None
        self.userLogin = None
        self.solutionUuid = None
        self.parentUuid = None
        self.commentText = None
        self.level = None
        self.createdDate = None
        self.modifiedDate = None

    def from_record(self, record):
        self.id = record[0]
        self.uuid = record[1]
        self.userLogin = record[2]
        self.solutionUuid = record[3]
        self.parentUuid = record[4]
        self.commentText = record[5]
        self.level = record[6]
        self.createdDate = record[7].strftime('%Y-%m-%dT%H:%M:%S')
        self.modifiedDate = record[8].strftime('%Y-%m-%dT%H:%M:%S')
