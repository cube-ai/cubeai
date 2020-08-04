import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Ability} from '../model/ability.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class AbilityService {
    private resourceUrl = SERVER_API_URL + 'umm/api/deployments';

    constructor(private http: HttpClient) { }

    query(req?: any): Observable<HttpResponse<Ability[]>> {
        const options = createRequestOption(req);
        return this.http.get<Ability[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

}
