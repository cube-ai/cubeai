class Description:
    def __init__(self):
        self.id = None
        self.solutionUuid = None
        self.authorLogin = None
        self.content = None

    def from_record(self, record):
        self.id = record[0]
        self.solutionUuid = record[1]
        self.authorLogin = record[2]
        self.content = record[3]
