import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {SolutionFavorite} from '../model/solution-favorite.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class SolutionFavoriteService {
    private resourceUrl = SERVER_API_URL + 'umm/api/solution-favorites';

    constructor(private http: HttpClient) { }

    create(solutionFavorite: SolutionFavorite): Observable<HttpResponse<SolutionFavorite>> {
        return this.http.post<SolutionFavorite>(this.resourceUrl, solutionFavorite, { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<SolutionFavorite[]>> {
        const options = createRequestOption(req);
        return this.http.get<SolutionFavorite[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(solutionId: number): Observable<HttpResponse<any>> {
        return this.http.delete(`${this.resourceUrl}/${solutionId}`, { observe: 'response' });
    }

    deleteSolutionFavoriteBySolutionUuid(solutionUuid: string): Observable<HttpResponse<any>> {
        return this.http.delete(this.resourceUrl + '/uuid/' + solutionUuid, { observe: 'response' });
    }

    findFavoriteSolutionUuidList(userLogin: string): Observable<HttpResponse<string[]>> {
        return this.http.get<string[]>(this.resourceUrl + '/uuids', { params: {userLogin}, observe: 'response' });
    }

}
