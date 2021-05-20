import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class UmuClient {

    url = SERVER_API_URL + 'umu/api/data';

    constructor(
        private http: HttpClient,
    ) {}

    download_document(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'download_document',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    delete_document(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'delete_document',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

}
