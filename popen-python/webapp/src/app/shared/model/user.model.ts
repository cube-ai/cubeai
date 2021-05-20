export class User {
    public id?: any;
    public login?: string;
    public fullName?: string;
    public phone?: string;
    public email?: string;
    public activated?: boolean;
    public langKey?: string;
    public authorities?: any[];
    public createdBy?: string;
    public createdDate?: Date;
    public lastModifiedBy?: string;
    public lastModifiedDate?: Date;
    public password?: string;
    public imageUrl?: string;
    public activateUrlPrefix?: string;

    constructor(
        id?: any,
        login?: string,
        fullName?: string,
        phone?: string,
        email?: string,
        activated?: boolean,
        langKey?: string,
        authorities?: any[],
        createdBy?: string,
        createdDate?: Date,
        lastModifiedBy?: string,
        lastModifiedDate?: Date,
        password?: string
    ) {
        this.id = id ? id : null;
        this.login = login ? login : '';
        this.fullName = fullName ? fullName : '';
        this.phone = phone ? phone : '';
        this.email = email ? email : '';
        this.activated = activated ? activated : false;
        this.langKey = langKey ? langKey : 'en';
        this.authorities = authorities ? authorities : [];
        this.createdBy = createdBy ? createdBy : null;
        this.createdDate = createdDate ? createdDate : null;
        this.lastModifiedBy = lastModifiedBy ? lastModifiedBy : null;
        this.lastModifiedDate = lastModifiedDate ? lastModifiedDate : null;
        this.password = password ? password : '';
    }
}
