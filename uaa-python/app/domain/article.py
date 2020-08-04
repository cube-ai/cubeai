class Article:
    def __init__(self):
        self.id = None
        self.uuid = None
        self.authorLogin = ''
        self.authorName = ''
        self.subject1 = ''
        self.subject2 = ''
        self.subject3 = ''
        self.title = ''
        self.summary = ''
        self.tag1 = ''
        self.tag2 = ''
        self.tag3 = ''
        self.pictureUrl = ''
        self.content = ''
        self.displayOrder = 0
        self.createdDate = None
        self.modifiedDate = None

    def from_record(self, record):
        self.id = record[0]
        self.uuid = record[1]
        self.authorLogin = record[2]
        self.authorName = record[3]
        self.subject1 = record[4]
        self.subject2 = record[5]
        self.subject3 = record[6]
        self.title = record[7]
        self.summary = record[8]
        self.tag1 = record[9]
        self.tag2 = record[10]
        self.tag3 = record[11]
        self.pictureUrl = record[12]
        self.content = record[13]
        self.displayOrder = record[14]
        self.createdDate = record[15].strftime('%Y-%m-%dT%H:%M:%S')
        self.modifiedDate = record[16].strftime('%Y-%m-%dT%H:%M:%S')

    def complete_attrs(self):
        if not hasattr(self, 'authorLogin'):
            self.authorLogin = ''
        if not hasattr(self, 'authorName'):
            self.authorName = ''
        if not hasattr(self, 'subject1'):
            self.subject1 = ''
        if not hasattr(self, 'subject2'):
            self.subject2 = ''
        if not hasattr(self, 'subject3'):
            self.subject3 = ''
        if not hasattr(self, 'title'):
            self.title = ''
        if not hasattr(self, 'summary'):
            self.summary = ''
        if not hasattr(self, 'tag1'):
            self.tag1 = ''
        if not hasattr(self, 'tag2'):
            self.tag2 = ''
        if not hasattr(self, 'tag3'):
            self.tag3 = ''
        if not hasattr(self, 'pictureUrl'):
            self.pictureUrl = ''
        if not hasattr(self, 'content'):
            self.content = ''
        if not hasattr(self, 'displayOrder'):
            self.displayOrder = 0
