class Message:
    def __init__(self):
        self.id = None
        self.sender = None
        self.receiver = None
        self.subject = None
        self.content = None
        self.url = ''
        self.urgent = False
        self.viewed = False
        self.deleted = False
        self.createdDate = None
        self.modifiedDate = None

    def from_record(self, record):
        self.id = record[0]
        self.sender = record[1]
        self.receiver = record[2]
        self.subject = record[3]
        self.content = record[4]
        self.url = record[5]
        self.urgent = ord(record[6]) == 1
        self.viewed = ord(record[7]) == 1
        self.deleted = ord(record[8]) == 1
        self.createdDate = record[9].strftime('%Y-%m-%dT%H:%M:%S') if record[9] else None
        self.modifiedDate = record[10].strftime('%Y-%m-%dT%H:%M:%S') if record[10] else None
