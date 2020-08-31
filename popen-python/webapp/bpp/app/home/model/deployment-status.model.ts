export class DeploymentStatus {
    public limitsCpu?: string;
    public limitsMem?: string;
    public requestsCpu?: string;
    public requestsMem?: string;
    public replicas?: number;
    public replicasReady?: number;
}
