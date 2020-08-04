import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class MessageService {
    private resourceUrl = SERVER_API_URL + 'uaa/api/messages';

    constructor(private http: HttpClient) { }

    getUnreadCount(req?: any): Observable<HttpResponse<number>> {
        const options = createRequestOption(req);
        return this.http.get<number>(this.resourceUrl + '/unread-count', { params: options, observe: 'response' });
    }

}
