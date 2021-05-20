class Credit:
    def __init__(self):
        self.id = None
        self.userLogin = None
        self.credit = None

    def from_record(self, record):
        self.id = record[0]
        self.userLogin = record[1]
        self.credit = record[2]
