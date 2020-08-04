import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class RegisterService {

    constructor(private http: HttpClient) {}

    save(account: any): Observable<any> {
        return this.http.post(SERVER_API_URL + 'uaa/api/register', account);
    }
}
