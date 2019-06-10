import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {SolutionRating} from '../model/solution-rating.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class SolutionRatingService {
    private resourceUrl = SERVER_API_URL + 'umm/api/solution-ratings';

    constructor(private http: HttpClient) { }

    create(solutionRating: SolutionRating): Observable<HttpResponse<SolutionRating>> {
        return this.http.post<SolutionRating>(this.resourceUrl, solutionRating, { observe: 'response' });
    }

    updateScore(body: any): Observable<HttpResponse<SolutionRating>> {
        return this.http.put<SolutionRating>(this.resourceUrl + '/score', body, { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<SolutionRating[]>> {
        const options = createRequestOption(req);
        return this.http.get<SolutionRating[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

}
