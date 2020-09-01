import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class UmdClient {

    url = SERVER_API_URL + 'umd/api/data';

    constructor(
        private http: HttpClient,
    ) {}

    get_deployment_status(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_deployment_status',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_deployment_logs(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_deployment_logs',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    scale_deployment(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'scale_deployment',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    pause_deployment(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'pause_deployment',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    restart_deployment(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'restart_deployment',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    stop_deployment(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'stop_deployment',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

}
