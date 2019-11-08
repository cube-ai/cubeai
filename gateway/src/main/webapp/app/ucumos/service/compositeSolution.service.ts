import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Solution} from '../model/solution.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class CompositeSolutionService {
    private resourceUrl = SERVER_API_URL + 'umo/api/compositeSolutions';

    constructor(private http: HttpClient) { }
    build_url(verb, params): string {
        return this.resourceUrl + verb + '?' + Object.keys(params).map(function(k) {
            return k + '=' + encodeURIComponent(params[k]);
        }).join('&');
    }

    createCompositeSolution(body: any): Observable<HttpResponse<any>> {
        return this.http.post<any>(this.resourceUrl, body, { observe: 'response' });
    }

    closeCompositeSolution(userId: any, solId: any): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/cdumps/${solId}/${userId}`, { observe: 'response' });
    }

    deleteCompositeSolution(id: any): Observable<HttpResponse<any>> {
        return this.http.delete<any>(this.resourceUrl + '/' + id, { observe: 'response' });
    }

    updateProbeIndicator(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + '/cdumps/probeIndicator', body, { observe: 'response' });
    }

    clearCompositeSolution(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + '/cdumps', body, { observe: 'response' });
    }

    saveCompositeSolution(body: any): Observable<HttpResponse<any>> {
        return this.http.post<any>(this.resourceUrl + '/cdumps', body, { observe: 'response' });
    }

    validateCompositeSolution(body: any): Observable<HttpResponse<any>> {
        return this.http.post<any>(this.resourceUrl + '/bluePrint', body, { observe: 'response' });
    }
}
