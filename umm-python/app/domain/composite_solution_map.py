class CompositeSolutionMap:
    def __init__(self):
        self.id = None
        self.parentUuid = None
        self.childUuid = None

    def from_record(self, record):
        self.id = record[0]
        self.parentUuid = record[1]
        self.childUuid = record[2]

