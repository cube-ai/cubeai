import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {PublishRequest} from '../model/publish-request.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class PublishRequestService {
    private resourceUrl = SERVER_API_URL + 'umm/api/publish-requests';

    constructor(private http: HttpClient) { }

    create(publishRequest: PublishRequest): Observable<HttpResponse<PublishRequest>> {
        return this.http.post<PublishRequest>(this.resourceUrl, publishRequest, { observe: 'response' });
    }

    update(body: any): Observable<HttpResponse<PublishRequest>> {
        return this.http.put<PublishRequest>(this.resourceUrl, body, { observe: 'response' });
    }

    find(id: number): Observable<HttpResponse<PublishRequest>> {
        return this.http.get<PublishRequest>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<PublishRequest[]>> {
        const options = createRequestOption(req);
        return this.http.get<PublishRequest[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(publishRequestId: number): Observable<HttpResponse<any>> {
        return this.http.delete(`${this.resourceUrl}/${publishRequestId}`, { observe: 'response' });
    }

}
