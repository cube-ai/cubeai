import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import { SERVER_API_URL } from '../../app.constants';
import {Message} from '../model/message.model';
import {MessageDraft} from '../model/massage-draft.model';

@Injectable()
export class MessageService {
    private resourceUrl = SERVER_API_URL + 'uaa/api/messages';

    constructor(private http: HttpClient) { }

    sendMessage(receiver: string, subject: string, content: string, url: string, urgent: boolean): Observable<HttpResponse<any>> {
        const message = new Message();
        message.receiver = receiver;
        message.subject = subject;
        message.content = content;
        message.url = url;
        message.urgent = urgent;

        return this.http.post<Message>(this.resourceUrl + '/send', message, { observe: 'response' });
    }

    sendMulticast(messageDraft: MessageDraft): Observable<HttpResponse<any>> {
        return this.http.post(this.resourceUrl + '/multicast', messageDraft, { observe: 'response' });
    }

    updateMessageViewed(id, viewed): Observable<HttpResponse<void>> {
        return this.http.put<void>(
            this.resourceUrl + '/viewed',
            {},
            { params: {id, viewed}, observe: 'response' }
            );
    }

    updateMessageDeleted(id, deleted): Observable<HttpResponse<void>> {
        return this.http.put<void>(
            this.resourceUrl + '/deleted',
            {},
            { params: {id, deleted}, observe: 'response' }
        );
    }

    find(id: number): Observable<HttpResponse<Message>> {
        return this.http.get<Message>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<Message[]>> {
        const options = createRequestOption(req);
        return this.http.get<Message[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(messageId: number): Observable<HttpResponse<any>> {
        return this.http.delete(`${this.resourceUrl}/${messageId}`, { observe: 'response' });
    }

    getUnreadCount(req?: any): Observable<HttpResponse<number>> {
        const options = createRequestOption(req);
        return this.http.get<number>(this.resourceUrl + '/unread-count', { params: options, observe: 'response' });
    }

}
