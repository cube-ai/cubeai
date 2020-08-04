import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Star} from '../model/star.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class StarService {
    private resourceUrl = SERVER_API_URL + 'umm/api/stars';

    constructor(private http: HttpClient) { }

    create(star: Star): Observable<HttpResponse<Star>> {
        return this.http.post<Star>(this.resourceUrl, star, { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<Star[]>> {
        const options = createRequestOption(req);
        return this.http.get<Star[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(starId: number): Observable<HttpResponse<any>> {
        return this.http.delete(this.resourceUrl + '/' + starId, { observe: 'response' });
    }

    deleteStarByTargetUuid(targetUuid: string): Observable<HttpResponse<any>> {
        return this.http.delete(this.resourceUrl + '/uuid/' + targetUuid, { observe: 'response' });
    }

    findStaredUuidList(userLogin: string): Observable<HttpResponse<string[]>> {
        return this.http.get<string[]>(this.resourceUrl + '/uuids', { params: {userLogin}, observe: 'response' });
    }

}
