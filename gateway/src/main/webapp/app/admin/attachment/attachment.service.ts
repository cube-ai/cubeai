import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Attachment} from './attachment.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class AttachmentService {
    private resourceUrl = SERVER_API_URL + 'uaa/api/attachments';

    constructor(private http: HttpClient) { }

    query(req?: any): Observable<HttpResponse<Attachment[]>> {
        const options = createRequestOption(req);
        return this.http.get<Attachment[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(attachmentId: number): Observable<HttpResponse<any>> {
        return this.http.delete(`${this.resourceUrl}/${attachmentId}`, { observe: 'response' });
    }

}
