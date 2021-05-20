class GithubUser:
    def __init__(self):
        self.id = None
        self.githubLogin = None
        self.userLogin = None

    def from_record(self, record):
        self.id = record[0]
        self.githubLogin = record[1]
        self.userLogin = record[2]
