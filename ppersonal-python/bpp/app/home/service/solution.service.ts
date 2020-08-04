import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Solution} from '../model/solution.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class SolutionService {
    private resourceUrl = SERVER_API_URL + 'umm/api/solutions';

    constructor(private http: HttpClient) { }

    query(req?: any): Observable<HttpResponse<Solution[]>> {
        const options = createRequestOption(req);
        return this.http.get<Solution[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

}
