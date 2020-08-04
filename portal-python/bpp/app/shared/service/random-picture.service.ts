import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class RandomPictureService {
    private resourceUrl = SERVER_API_URL + 'uaa/api/randompicture';

    constructor(private http: HttpClient) { }

    getRandomPicture(width: number, height: number): Observable<HttpResponse<any>> {
        return this.http.get<any>(this.resourceUrl + '/' + width + '/' + height, { observe: 'response' });
    }

}
