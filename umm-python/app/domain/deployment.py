class Deployment:
    def __init__(self):
        self.id = None
        self.uuid = None
        self.deployer = None
        self.solutionUuid = None
        self.solutionName = None
        self.solutionAuthor = None
        self.k8sPort = None
        self.isPublic = None
        self.status = None
        self.createdDate = None
        self.modifiedDate = None
        self.pictureUrl = None
        self.starCount = None
        self.callCount = None
        self.demoUrl = None
        self.subject1 = None
        self.subject2 = None
        self.subject3 = None
        self.displayOrder = None

    def from_record(self, record):
        self.id = record[0]
        self.uuid = record[1]
        self.deployer = record[2]
        self.solutionUuid = record[3]
        self.solutionName = record[4]
        self.solutionAuthor = record[5]
        self.k8sPort = record[6]
        self.isPublic = ord(record[7]) == 1
        self.status = record[8]
        self.createdDate = record[9].strftime('%Y-%m-%dT%H:%M:%S')
        self.modifiedDate = record[10].strftime('%Y-%m-%dT%H:%M:%S')
        self.pictureUrl = record[11]
        self.starCount = record[12]
        self.callCount = record[13]
        self.demoUrl = record[14]
        self.subject1 = record[15]
        self.subject2 = record[16]
        self.subject3 = record[17]
        self.displayOrder = record[18]
