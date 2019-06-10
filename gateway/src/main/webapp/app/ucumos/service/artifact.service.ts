import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Artifact} from '../model/artifact.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class ArtifactService {
    private resourceUrl = SERVER_API_URL + 'umm/api/artifacts';

    constructor(private http: HttpClient) { }

    query(req?: any): Observable<HttpResponse<Artifact[]>> {
        const options = createRequestOption(req);
        return this.http.get<Artifact[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

}
