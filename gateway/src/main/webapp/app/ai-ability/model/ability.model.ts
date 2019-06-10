export class Ability {
    public id?: number;
    public uuid?: string;
    public deployer?: string;
    public solutionUuid?: string;
    public solutionName?: string;
    public solutionAuthor?: string;
    public k8sPort?: number;
    public isPublic?: boolean;
    public status?: string;
    public createdDate?: Date;
    public modifiedDate?: Date;
    public pictureUrl: string;
    public modelType: string;
    public toolkitType: string;
    public callCount: number;
    public demoUrl: string;
}
