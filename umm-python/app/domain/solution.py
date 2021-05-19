class Solution:
    def __init__(self):
        self.id = None
        self.uuid = None
        self.authorLogin = None
        self.authorName = None
        self.company = None
        self.name = None
        self.version = None
        self.summary = None
        self.tag1 = None
        self.tag2 = None
        self.tag3 = None
        self.subject1 = None
        self.subject2 = None
        self.subject3 = None
        self.displayOrder = None
        self.pictureUrl = None
        self.active = None
        self.modelType = None
        self.toolkitType = None
        self.starCount = None
        self.viewCount = None
        self.commentCount = None
        self.createdDate = None
        self.modifiedDate = None
        self.hasWeb =None
        self.deployStatus = None
        self.deployer = None
        self.deployDate = None
        self.k8sPort = None
        self.callCount = None

    def from_record(self, record):
        self.id = record[0]
        self.uuid = record[1]
        self.authorLogin = record[2]
        self.authorName = record[3]
        self.company = record[4]
        self.name = record[5]
        self.version = record[6]
        self.summary = record[7]
        self.tag1 = record[8]
        self.tag2 = record[9]
        self.tag3 = record[10]
        self.subject1 = record[11]
        self.subject2 = record[12]
        self.subject3 = record[13]
        self.displayOrder = record[14]
        self.pictureUrl = record[15]
        self.active = ord(record[16]) == 1
        self.modelType = record[17]
        self.toolkitType = record[18]
        self.starCount = record[19]
        self.viewCount = record[20]
        self.commentCount = record[21]
        self.createdDate = record[22].strftime('%Y-%m-%dT%H:%M:%S')
        self.modifiedDate = record[23].strftime('%Y-%m-%dT%H:%M:%S')
        self.hasWeb = ord(record[24]) == 1
        self.deployStatus = record[25]
        self.deployer = record[26]
        self.deployDate = record[27].strftime('%Y-%m-%dT%H:%M:%S')
        self.k8sPort = record[28]
        self.callCount = record[29]
