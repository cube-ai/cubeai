import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Description} from '../model/description.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class DescriptionService {
    private resourceUrl = SERVER_API_URL + 'umm/api/descriptions';

    constructor(private http: HttpClient) { }

    updateContent(body: any): Observable<HttpResponse<Description>> {
        return this.http.put<Description>(this.resourceUrl + '/content', body, { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<Description[]>> {
        const options = createRequestOption(req);
        return this.http.get<Description[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

}
