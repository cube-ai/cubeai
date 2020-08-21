import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';
import {createRequestOption} from '../../shared';

@Injectable()
export class DownloadService {
    private resourceUrl = SERVER_API_URL + 'umu/api';

    constructor(private http: HttpClient) { }

    download(url: string): Observable<any> {
        const options = createRequestOption({
            url,
        });

        return this.http.get(this.resourceUrl + '/download',  {params: options, observe: 'response', responseType: 'text'} );
    }

}
