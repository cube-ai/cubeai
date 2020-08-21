class DeploymentStatus:
    def __init__(self):
        self.limitsCpu = 0
        self.limitsMem = 0
        self.requestsCpu = 0
        self.requestsMem = 0
        self.replicas = 0
        self.replicasReady = 0
