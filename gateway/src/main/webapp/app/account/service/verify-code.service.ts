import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class VerifyCodeService {
    private resourceUrl = SERVER_API_URL + 'uaa/api/verify-codes';

    constructor(private http: HttpClient) { }

    getVerifyCode(): Observable<HttpResponse<string>> {
        return this.http.get<string>(this.resourceUrl, { observe: 'response' });
    }

}
