import { Injectable } from '@angular/core';
import { Principal } from './principal.service';
import { AuthServerProvider } from './auth-jwt.service';

@Injectable()
export class LoginService {

    constructor(
        private principal: Principal,
        private authServerProvider: AuthServerProvider,
    ) {}

    login(credentials, callback?) {
        const cb = callback || function() {};

        return new Promise((resolve, reject) => {
            this.authServerProvider.login(credentials).subscribe((data) => {
                this.principal.updateCurrentAccount().then((account) => {
                    resolve(data);
                });
                return cb();
            }, (err) => {
                this.logout();
                reject(err);
                return cb(err);
            });
        });
    }

    logout(): Promise<boolean> {
        if (this.principal.isAuthenticated()) {
            return this.authServerProvider.logout().toPromise().then(
                () => {
                    this.principal.authenticate(null);
                    return true;
                }, () => {
                    return false;
                });
        } else {
            this.principal.authenticate(null);
            return Promise.resolve(true);
        }
    }
}
