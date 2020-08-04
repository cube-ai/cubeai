export class Message {
    public id?: number;
    public sender?: string;
    public receiver?: string;
    public subject?: string;
    public content?: string;
    public url?: string;
    public urgent?: boolean;
    public viewed?: boolean;
    public deleted?: boolean;
    public createdDate?: Date;
    public modifiedDate?: Date;
}
