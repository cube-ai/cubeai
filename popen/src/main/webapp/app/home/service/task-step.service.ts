import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {TaskStep} from '../model/task-step.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class TaskStepService {
    private resourceUrl = SERVER_API_URL + 'umm/api/task-steps';

    constructor(private http: HttpClient) { }

    query(req?: any): Observable<HttpResponse<TaskStep[]>> {
        const options = createRequestOption(req);
        return this.http.get<TaskStep[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

}
