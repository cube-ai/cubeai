class Document:
    def __init__(self):
        self.id = None
        self.solutionUuid = None
        self.authorLogin = None
        self.name = None
        self.url = None
        self.fileSize = None
        self.createdDate = None
        self.modifiedDate = None

    def from_record(self, record):
        self.id = record[0]
        self.solutionUuid = record[1]
        self.authorLogin = record[2]
        self.name = record[3]
        self.url = record[4]
        self.fileSize = record[5]
        self.createdDate = record[6].strftime('%Y-%m-%dT%H:%M:%S')
        self.modifiedDate = record[7].strftime('%Y-%m-%dT%H:%M:%S')
