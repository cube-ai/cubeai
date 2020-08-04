import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Credit} from '../model/credit.model';
import {CreditHistory} from '../model/credit-history.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class CreditService {
    private resourceUrl = SERVER_API_URL + 'umm/api/';

    constructor(private http: HttpClient) { }

    queryCredit(req?: any): Observable<HttpResponse<Credit>> {
        return this.http.get<Credit>(this.resourceUrl + 'credits/myself', { observe: 'response' });
    }

    queryAllCredits(req?: any): Observable<HttpResponse<Credit[]>> {
        const options = createRequestOption(req);
        return this.http.get<Credit[]>(this.resourceUrl + 'credits', { params: options, observe: 'response' });
    }

    queryCreditHistory(req?: any): Observable<HttpResponse<CreditHistory[]>> {
        const options = createRequestOption(req);
        return this.http.get<CreditHistory[]>(this.resourceUrl + 'credit-histories', { params: options, observe: 'response' });
    }

    update(id: number, plus: number): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + 'credits/' + id + '/' + plus, {}, { observe: 'response' });
    }

}
