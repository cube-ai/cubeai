import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class LoginService {

    url = SERVER_API_URL + 'api/data';

    constructor(
        private http: HttpClient,
    ) {}

    login(credentials): Observable<HttpResponse<any>> {
        const body = {
            action: 'login',
            args: credentials,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    logout(): Observable<HttpResponse<any>> {
        const body = {
            action: 'logout',
            args: {},
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

}
