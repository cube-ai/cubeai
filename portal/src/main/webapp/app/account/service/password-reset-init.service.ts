import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class PasswordResetInitService {

    constructor(private http: HttpClient) {}

    save(mailAndVerifyCode: any): Observable<any> {
        return this.http.post(SERVER_API_URL + 'uaa/api/account/reset-password/init', mailAndVerifyCode);
    }
}
