class Attachment:
    def __init__(self):
        self.id = None
        self.authorLogin = ''
        self.name = ''
        self.url = ''
        self.fileSize = 0
        self.createdDate = None
        self.modifiedDate = None

    def from_record(self, record):
        self.id = record[0]
        self.authorLogin = record[1]
        self.name = record[2]
        self.url = record[3]
        self.fileSize = record[4]
        self.createdDate = record[5].strftime('%Y-%m-%dT%H:%M:%S')
        self.modifiedDate = record[6].strftime('%Y-%m-%dT%H:%M:%S')
