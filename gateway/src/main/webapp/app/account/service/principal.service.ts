import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import { AccountService } from './account.service';
import {User} from '..';

@Injectable()
export class Principal {
    private authenticated = false;
    private authenticationState = new Subject<any>();
    private currentAccount: User = null;

    constructor(
        private accountService: AccountService,
    ) {}

    authenticate(currentAccount) {
        this.currentAccount = currentAccount;
        this.authenticated = currentAccount !== null;
        this.authenticationState.next(this.currentAccount);
    }

    hasAnyAuthority(authorities: string[]): Promise<boolean> {
        return Promise.resolve(this.hasAnyAuthorityDirect(authorities));
    }

    hasAnyAuthorityDirect(authorities: string[]): boolean {
        if (!this.authenticated || !this.currentAccount || !this.currentAccount.authorities) {
            return false;
        }

        for (let i = 0; i < authorities.length; i++) {
            if (this.currentAccount.authorities.includes(authorities[i])) {
                return true;
            }
        }

        return false;
    }

    hasAuthorityAsync(authority: string): Promise<boolean> {
        if (!this.authenticated) {
           return Promise.resolve(false);
        }

        return this.updateCurrentAccount().then((currentAccount) => {
            return Promise.resolve(currentAccount.authorities && currentAccount.authorities.includes(authority));
        }, () => {
            return Promise.resolve(false);
        });
    }

    hasAuthority(authority: string): boolean {
        if (!this.authenticated || !this.currentAccount || !this.currentAccount.authorities) {
            return false;
        }

        return this.currentAccount.authorities.includes(authority);
    }

    isAuthenticated(): boolean {
        return this.authenticated;
    }

    isCurrentAccountResolved(): boolean {
        return !!this.currentAccount;
    }

    getAuthenticationState(): Observable<any> {
        return this.authenticationState.asObservable();
    }

    getImageUrl(): String {
        return this.isCurrentAccountResolved() ? this.currentAccount.imageUrl : null;
    }

    updateCurrentAccount(): Promise<User> {
        // retrieve the currentAccount data from the server, update the currentAccount object, and then resolve.
        return this.accountService.get().toPromise().then((response) => {
            const account: User = response.body;
            if (account) {
                this.currentAccount = account;
                this.authenticated = true;
            } else {
                this.currentAccount = null;
                this.authenticated = false;
            }
            this.authenticationState.next(this.currentAccount);
            return this.currentAccount;
        }).catch((err) => {
            this.currentAccount = null;
            this.authenticated = false;
            this.authenticationState.next(this.currentAccount);
            return null;
        });
    }

    getCurrentAccount(): User {
        return this.currentAccount;
    }

}
