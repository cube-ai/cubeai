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

    updateBaseinfo(body: any): Observable<HttpResponse<Solution>> {
        return this.http.put<Solution>(this.resourceUrl + '/baseinfo', body, { observe: 'response' });
    }

    updateActive(body: any): Observable<HttpResponse<Solution>> {
        return this.http.put<Solution>(this.resourceUrl + '/active', body, { observe: 'response' });
    }

    requestPublish(body: any): Observable<HttpResponse<Solution>> {
        return this.http.put<Solution>(this.resourceUrl + '/publish-request', body, { observe: 'response' });
    }

    approvePublish(body: any): Observable<HttpResponse<Solution>> {
        return this.http.put<Solution>(this.resourceUrl + '/publish-approve', body, { observe: 'response' });
    }

    updateSubjects(body: any): Observable<HttpResponse<Solution>> {
        return this.http.put<Solution>(this.resourceUrl + '/subjects', body, { observe: 'response' });
    }

    updateRatingStats(body: any): Observable<HttpResponse<Solution>> {
        return this.http.put<Solution>(this.resourceUrl + '/rating-stats', body, { observe: 'response' });
    }

    updateCommentCount(body: any): Observable<HttpResponse<Solution>> {
        return this.http.put<Solution>(this.resourceUrl + '/comment-count', body, { observe: 'response' });
    }

    updateViewCount(body: any): Observable<HttpResponse<Solution>> {
        return this.http.put<Solution>(this.resourceUrl + '/view-count', body, { observe: 'response' });
    }

    updateDownloadCount(body: any): Observable<HttpResponse<Solution>> {
        return this.http.put<Solution>(this.resourceUrl + '/download-count', body, { observe: 'response' });
    }

    find(id: number): Observable<HttpResponse<Solution>> {
        return this.http.get<Solution>(this.resourceUrl + '/' + id, { observe: 'response' });
    }

    getPictureUrl(id: number): Observable<HttpResponse<any>> {
        return this.http.get<any>(this.resourceUrl + '/' + id + '/picture-url', { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<Solution[]>> {
        const options = createRequestOption(req);
        return this.http.get<Solution[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete(this.resourceUrl + '/' + id, { observe: 'response' });
    }

}
