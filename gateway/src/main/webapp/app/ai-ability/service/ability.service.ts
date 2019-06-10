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

    queryAll(req?: any): Observable<HttpResponse<Ability[]>> {
        const options = createRequestOption(req);
        return this.http.get<Ability[]>(this.resourceUrl + '/all', { params: options, observe: 'response' });
    }

    update(body: any): Observable<HttpResponse<Ability>> {
        return this.http.put<Ability>(this.resourceUrl, body, { observe: 'response' });
    }

    stop(body: any): Observable<HttpResponse<Ability>> {
        return this.http.put<Ability>(this.resourceUrlLcm + '/stop', body, { observe: 'response' });
    }

}
