export class Comment {
    public id?: number;
    public uuid?: string;
    public userLogin?: string;
    public solutionUuid?: string;
    public parentUuid?: string;
    public commentText?: string;
    public level?: number;
    public createdDate?: Date;
    public modifiedDate?: Date;

    // not in database
    public viewReply?: boolean;
    public replyComments?: Comment[];
    public isReplying?: boolean;
    public replyText?: string;
}
