import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Application} from '../model/application.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class ApplicationService {
    private resourceUrl = SERVER_API_URL + 'uaa/api/applications';

    constructor(private http: HttpClient) { }

    create(application: Application): Observable<HttpResponse<Application>> {
        return this.http.post<Application>(this.resourceUrl, application, { observe: 'response' });
    }

    update(application: Application): Observable<HttpResponse<Application>> {
        return this.http.put<Application>(this.resourceUrl, application, { observe: 'response' });
    }

    find(id: number): Observable<HttpResponse<Application>> {
        return this.http.get<Application>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<Application[]>> {
        const options = createRequestOption(req);
        return this.http.get<Application[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(applicationId: number): Observable<HttpResponse<any>> {
        return this.http.delete(`${this.resourceUrl}/${applicationId}`, { observe: 'response' });
    }

    queryAppWithPictures(req?: any): Observable<HttpResponse<Application[]>> {
        const options = createRequestOption(req);
        return this.http.get<Application[]>(SERVER_API_URL + 'uaa/api/applicationsp', { params: options, observe: 'response' });
    }

}
