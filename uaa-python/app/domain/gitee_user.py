class GiteeUser:
    def __init__(self):
        self.id = None
        self.giteeLogin = None
        self.userLogin = None

    def from_record(self, record):
        self.id = record[0]
        self.giteeLogin = record[1]
        self.userLogin = record[2]
