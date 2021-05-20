class User:
    def __init__(self):
        self.id = None
        self.login = None
        self.password = None
        self.fullName = None
        self.phone = None
        self.email = None
        self.imageUrl = ''
        self.activated = False
        self.langKey = 'en'
        self.activationKey = None
        self.resetKey = None
        self.createdBy = None
        self.createdDate = None
        self.resetDate = None
        self.lastModifiedBy = None
        self.lastModifiedDate = None
        self.authorities = ['ROLE_USER', ]
        self.activateUrlPrefix = None

    def from_record(self, record):
        self.id = record[0]
        self.login = record[1]
        self.password = record[2]
        self.fullName = record[3]
        self.phone = record[4]
        self.email = record[5]
        self.imageUrl = record[6]
        self.activated = ord(record[7]) == 1
        self.langKey = record[8]
        self.activationKey = record[9]
        self.resetKey = record[10]
        self.createdBy = record[11]
        self.createdDate = record[12].strftime('%Y-%m-%dT%H:%M:%S') if record[12] else None
        self.resetDate = record[13].strftime('%Y-%m-%dT%H:%M:%S') if record[13] else None
        self.lastModifiedBy = record[14]
        self.lastModifiedDate = record[15].strftime('%Y-%m-%dT%H:%M:%S') if record[15] else None
        self.authorities = record[16].split(',')

    def remove_internal_values(self):
        delattr(self, 'password')
        delattr(self, 'activationKey')
        delattr(self, 'resetKey')
        delattr(self, 'resetDate')
        delattr(self, 'activateUrlPrefix')
