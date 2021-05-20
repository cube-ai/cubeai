class Application:
    def __init__(self):
        self.id = None
        self.uuid = None
        self.name = ''
        self.summary = ''
        self.url = ''
        self.pictureUrl = ''
        self.subject1 = ''
        self.subject2 = ''
        self.subject3 = ''
        self.owner = ''
        self.needRoles = ''
        self.displayOrder = 0
        self.createdBy = ''
        self.modifiedBy = ''
        self.createdDate = None
        self.modifiedDate = None

    def from_record(self, record):
        self.id = record[0]
        self.uuid = record[1]
        self.name = record[2]
        self.summary = record[3]
        self.url = record[4]
        self.pictureUrl = record[5]
        self.subject1 = record[6]
        self.subject2 = record[7]
        self.subject3 = record[8]
        self.owner = record[9]
        self.needRoles = record[10]
        self.displayOrder = record[11]
        self.createdBy = record[12]
        self.modifiedBy = record[13]
        self.createdDate = record[14].strftime('%Y-%m-%dT%H:%M:%S')
        self.modifiedDate = record[15].strftime('%Y-%m-%dT%H:%M:%S')

    def complete_attrs(self):
        if not hasattr(self, 'name'):
            self.name = ''
        if not hasattr(self, 'summary'):
            self.summary = ''
        if not hasattr(self, 'url'):
            self.url = ''
        if not hasattr(self, 'pictureUrl'):
            self.pictureUrl = ''
        if not hasattr(self, 'subject1'):
            self.subject1 = ''
        if not hasattr(self, 'subject2'):
            self.subject2 = ''
        if not hasattr(self, 'subject3'):
            self.subject3 = ''
        if not hasattr(self, 'owner'):
            self.owner = ''
        if not hasattr(self, 'needRoles'):
            self.needRoles = ''
        if not hasattr(self, 'displayOrder'):
            self.displayOrder = 0
