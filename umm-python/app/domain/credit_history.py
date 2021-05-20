class CreditHistory:
    def __init__(self):
        self.id = None
        self.userLogin = None
        self.creditPlus = None
        self.currentCredit = None
        self.comment = None
        self.modifyDate = None

    def from_record(self, record):
        self.id = record[0]
        self.userLogin = record[1]
        self.creditPlus = record[2]
        self.currentCredit = record[3]
        self.comment = record[4]
        self.modifyDate = record[5].strftime('%Y-%m-%dT%H:%M:%S')

