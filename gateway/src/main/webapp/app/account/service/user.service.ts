import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { User } from '../model/user.model';
import { createRequestOption } from '../../shared/service/request-util';

@Injectable()
export class UserService {
    private resourceUrl = SERVER_API_URL + 'uaa/api/users';

    constructor(private http: HttpClient) { }

    create(user: User): Observable<HttpResponse<User>> {
        return this.http.post<User>(this.resourceUrl, user, { observe: 'response' });
    }

    update(user: User): Observable<HttpResponse<User>> {
        return this.http.put<User>(this.resourceUrl, user, { observe: 'response' });
    }

    find(login: string): Observable<HttpResponse<User>> {
        return this.http.get<User>(`${this.resourceUrl}/${login}`, { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<User[]>> {
        const options = createRequestOption(req);
        return this.http.get<User[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(login: string): Observable<HttpResponse<any>> {
        return this.http.delete(`${this.resourceUrl}/${login}`, { observe: 'response' });
    }

    getLoginExist(login: string): Observable<HttpResponse<number>> {
        return this.http.get<number>(`${this.resourceUrl}/exist/login/${login}`, { observe: 'response' });
    }

    getEmailExist(email: string): Observable<HttpResponse<number>> {
        return this.http.get<number>(`${this.resourceUrl}/exist/email/${email}`, { observe: 'response' });
    }

    getPhoneExist(phone: string): Observable<HttpResponse<number>> {
        return this.http.get<number>(`${this.resourceUrl}/exist/phone/${phone}`, { observe: 'response' });
    }

    getAuthorities(): Observable<HttpResponse<string[]>> {
        return this.http.get<string[]>(this.resourceUrl + '/authorities', { observe: 'response' });
    }

    createAuthority(authority: string): Observable<HttpResponse<any>> {
        return this.http.post<any>(this.resourceUrl + '/authorities/' + authority, {}, { observe: 'response' });
    }

    deleteAuthority(authority: string): Observable<HttpResponse<any>> {
        return this.http.delete(this.resourceUrl + '/authorities/' + authority, { observe: 'response' });
    }

}
