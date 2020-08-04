import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Ability} from '../model/ability.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class AbilityService {
    private resourceUrl = SERVER_API_URL + 'umm/api/deployments';
    private resourceUrlLcm = SERVER_API_URL + 'umd/api/lcm';

    constructor(private http: HttpClient) { }

    find(id: number): Observable<HttpResponse<Ability>> {
        return this.http.get<Ability>(this.resourceUrl + '/' + id, { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<Ability[]>> {
        const options = createRequestOption(req);
        return this.http.get<Ability[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    updateDemoUrl(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + '/demourl', body, { observe: 'response' });
    }

    updateAdminInfo(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + '/admininfo', body, { observe: 'response' });
    }

    updateSolutionInfo(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + '/solutioninfo', body, { observe: 'response' });
    }

    updateStarCount(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + '/star-count', body, { observe: 'response' });
    }

    stop(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrlLcm + '/stop', body, { observe: 'response' });
    }

}
