import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Task} from '../model/task.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class TaskService {
    private resourceUrl = SERVER_API_URL + 'umm/api/tasks';

    constructor(private http: HttpClient) { }

    query(req?: any): Observable<HttpResponse<Task[]>> {
        const options = createRequestOption(req);
        return this.http.get<Task[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

}
