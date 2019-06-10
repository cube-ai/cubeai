import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class OnboardingService {
    private resourceUrlOnboarding = SERVER_API_URL + 'umu/api/onboarding';
    private resourceUrlModelFile = SERVER_API_URL + 'umu/api/modelfile';

    constructor(private http: HttpClient) { }

    onboarding(taskUuid: string): Observable<HttpResponse<any>> {
        return this.http.post<any>(this.resourceUrlOnboarding + '/' + taskUuid,  {}, { observe: 'response' });
    }

    deleteModelFile(taskUuid: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(this.resourceUrlModelFile + '/' + taskUuid,  { observe: 'response' });
    }

}
