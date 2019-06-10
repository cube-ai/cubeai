import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {SolutionShared} from '../model/solution-shared.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class SolutionSharedService {
    private resourceUrl = SERVER_API_URL + 'umm/api/solution-shareds';

    constructor(private http: HttpClient) { }

    create(solutionShared: SolutionShared): Observable<HttpResponse<SolutionShared>> {
        return this.http.post<SolutionShared>(this.resourceUrl, solutionShared, { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<SolutionShared[]>> {
        const options = createRequestOption(req);
        return this.http.get<SolutionShared[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(solutionId: number): Observable<HttpResponse<any>> {
        return this.http.delete(`${this.resourceUrl}/${solutionId}`, { observe: 'response' });
    }

}
