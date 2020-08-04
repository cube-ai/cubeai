import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Credit} from '../model/credit.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class CreditService {
    private resourceUrl = SERVER_API_URL + 'umm/api/';

    constructor(private http: HttpClient) { }

    queryCredit(req?: any): Observable<HttpResponse<Credit>> {
        return this.http.get<Credit>(this.resourceUrl + 'credits/myself', { observe: 'response' });
    }

}
