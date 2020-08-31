import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';
import Any = jasmine.Any;

@Injectable()
export class HttpService {

    constructor(
        private http: HttpClient,
    ) {}

    post(service, body): Observable<HttpResponse<Any>> {
        const url = SERVER_API_URL + service + '/api/data';
        return this.http.post<Any>(url, body, { observe: 'response' });
    }

    login(credentials): Observable<HttpResponse<Any>> {
        const url = SERVER_API_URL + 'api/data';
        const body = {
            action: 'login',
            args: credentials,
        };
        return this.http.post<Any>(url, body, { observe: 'response' });
    }

    logout(): Observable<HttpResponse<Any>> {
        const url = SERVER_API_URL + 'api/data';
        const body = {
            action: 'logout',
            args: {},
        };
        return this.http.post<Any>(url, body, { observe: 'response' });
    }

}
