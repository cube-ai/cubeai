import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class DeployService {
    private resourceUrl = SERVER_API_URL + 'umd/api/deploy';

    constructor(private http: HttpClient) { }

    deploy(body: any): Observable<HttpResponse<any>> {
        return this.http.post<any>(this.resourceUrl,  body, { observe: 'response' });
    }

}
