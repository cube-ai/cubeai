import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Document} from '../model/document.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class DocumentService {
    private resourceUrlUmm = SERVER_API_URL + 'umm/api/documents';
    private resourceUrlUmu = SERVER_API_URL + 'umu/api/documents';

    constructor(private http: HttpClient) { }

    query(req?: any): Observable<HttpResponse<Document[]>> {
        const options = createRequestOption(req);
        return this.http.get<Document[]>(this.resourceUrlUmm, { params: options, observe: 'response' });
    }

    deleteDocument(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(this.resourceUrlUmu + '/' + id, { observe: 'response' });
    }

}
