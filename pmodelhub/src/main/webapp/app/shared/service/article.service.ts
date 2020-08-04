import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Article} from '../model/article.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class ArticleService {
    private resourceUrl = SERVER_API_URL + 'uaa/api/articles';

    constructor(private http: HttpClient) { }

    create(article: Article): Observable<HttpResponse<Article>> {
        return this.http.post<Article>(this.resourceUrl, article, { observe: 'response' });
    }

    update(article: Article): Observable<HttpResponse<Article>> {
        return this.http.put<Article>(this.resourceUrl, article, { observe: 'response' });
    }

    find(id: number): Observable<HttpResponse<Article>> {
        return this.http.get<Article>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<Article[]>> {
        const options = createRequestOption(req);
        return this.http.get<Article[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(articleId: number): Observable<HttpResponse<any>> {
        return this.http.delete(`${this.resourceUrl}/${articleId}`, { observe: 'response' });
    }

}
