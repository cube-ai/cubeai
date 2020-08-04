import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class AuthServerProvider {
    constructor(
        private http: HttpClient    ) {}

    getToken() {
        return null;
    }

    login(credentials): Observable<any> {
        return this.http.post(SERVER_API_URL + 'auth/login', credentials, {});
    }

    loginWithToken(jwt, rememberMe) {
        if (jwt) {
            this.storeAuthenticationToken(jwt, rememberMe);
            return Promise.resolve(jwt);
        } else {
            return Promise.reject('auth-jwt-service Promise reject'); // Put appropriate error message here
        }
    }

    storeAuthenticationToken(jwt, rememberMe) {
    }

    logout(): Observable<any> {
        return this.http.post(SERVER_API_URL + 'auth/logout', null);
    }
}
