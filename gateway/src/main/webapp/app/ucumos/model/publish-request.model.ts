export class PublishRequest {
    public id?: number;
    public solutionUuid?: string;
    public solutionName?: string;
    public requestUserLogin?: string;
    public requestType?: string;
    public requestReason?: string;
    public requestDate?: Date;
    public reviewed?: boolean;
    public reviewUserLogin?: string;
    public reviewDate?: Date;
    public reviewResult?: String;
    public reviewComment?: string;
}
