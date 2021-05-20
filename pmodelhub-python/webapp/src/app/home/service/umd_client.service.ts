import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class UmdClient {

    url = SERVER_API_URL + 'umd/api/data';

    constructor(
        private http: HttpClient,
    ) {}

    deploy_model(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'deploy_model',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

}
