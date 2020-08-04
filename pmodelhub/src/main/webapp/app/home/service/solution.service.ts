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

    updateBaseinfo(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + '/baseinfo', body, { observe: 'response' });
    }

    updateActive(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + '/active', body, { observe: 'response' });
    }

    updatePictureUrl(body: any): Observable<HttpResponse<Solution>> {
        return this.http.put<Solution>(this.resourceUrl + '/picture-url', body, { observe: 'response' });
    }

    updateAdminInfo(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + '/admininfo', body, { observe: 'response' });
    }

    updateStarCount(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + '/star-count', body, { observe: 'response' });
    }

    updateCommentCount(body: any): Observable<HttpResponse<Solution>> {
        return this.http.put<Solution>(this.resourceUrl + '/comment-count', body, { observe: 'response' });
    }

    updateViewCount(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + '/view-count', body, { observe: 'response' });
    }

    updateDownloadCount(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + '/download-count', body, { observe: 'response' });
    }

    find(id: number): Observable<HttpResponse<Solution>> {
        return this.http.get<Solution>(this.resourceUrl + '/' + id, { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<Solution[]>> {
        const options = createRequestOption(req);
        return this.http.get<Solution[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete(this.resourceUrl + '/' + id, { observe: 'response' });
    }

}
