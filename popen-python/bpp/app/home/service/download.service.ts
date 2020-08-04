import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';
import {createRequestOption} from '../../shared';

@Injectable()
export class DownloadService {
    private resourceUrl = SERVER_API_URL + 'umu/api';

    constructor(private http: HttpClient) { }

    getMetadataText(solutionUuid: string): Observable<HttpResponse<any>> {
        return this.http.get<any>(this.resourceUrl + '/artifact/metadata/' + solutionUuid,  { observe: 'response' });
    }

    getProtobufText(solutionUuid: string): Observable<HttpResponse<any>> {
        return this.http.get<any>(this.resourceUrl + '/artifact/protobuf/' + solutionUuid,  { observe: 'response' });
    }

    getFileText(url: string): Observable<HttpResponse<any>> {
        const options = createRequestOption({
            url,
        });
        return this.http.get<any>(this.resourceUrl + '/get-file-text',  {params: options, observe: 'response'});
    }

    downloadFile(url: string): Observable<any> {
        const options = createRequestOption({
            url,
        });

        return this.http.get(this.resourceUrl + '/download',  {params: options, observe: 'response', responseType: 'blob'} );
    }

}
