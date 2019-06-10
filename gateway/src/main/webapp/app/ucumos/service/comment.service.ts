import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Comment} from '../model/comment.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class CommentService {
    private resourceUrl = SERVER_API_URL + 'umm/api/comments';

    constructor(private http: HttpClient) { }

    create(comment: Comment): Observable<HttpResponse<Comment>> {
        return this.http.post<Comment>(this.resourceUrl, comment, { observe: 'response' });
    }

    find(id: number): Observable<HttpResponse<Comment>> {
        return this.http.get<Comment>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<Comment[]>> {
        const options = createRequestOption(req);
        return this.http.get<Comment[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(commentId: number): Observable<HttpResponse<any>> {
        return this.http.delete(`${this.resourceUrl}/${commentId}`, { observe: 'response' });
    }

}
