import { Injectable } from '@angular/core';
import { UaaClient } from './uaa_client.service';
import {User} from '..';

@Injectable()
export class Principal {
    private authenticated = false;
    private currentAccount: User = null;

    constructor(
        private uaaClient: UaaClient,
    ) {}

    authenticate(currentAccount) {
        this.currentAccount = currentAccount;
        this.authenticated = !!currentAccount;
    }

    isAuthenticated(): boolean {
        return this.authenticated;
    }

    getCurrentAccount(): User {
        return this.currentAccount;
    }

    getLogin(): string {
        return this.authenticated ? this.currentAccount.login : null;
    }

    getFullName(): string {
        return this.authenticated ? this.currentAccount.fullName : null;
    }

    getEmail(): string {
        return this.authenticated ? this.currentAccount.email : null;
    }

    getPhone(): string {
        return this.authenticated ? this.currentAccount.phone : null;
    }

    getImageUrl(): String {
        return this.authenticated ? this.currentAccount.imageUrl : null;
    }

    getAuthorities(): string[] {
        return this.authenticated ? this.currentAccount.authorities : null;
    }

    hasAuthority(authority: string): boolean {
        if (!this.authenticated || !this.currentAccount || !this.currentAccount.authorities) {
            return false;
        }

        return this.currentAccount.authorities.includes(authority);
    }

    hasAnyAuthority(authorities: string[]): boolean {
        if (!authorities || authorities.length < 1) {
            return true;
        }

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

    updateCurrentAccount(): Promise<User> {
        return this.uaaClient.get_current_account({}).toPromise().then((res) => {
            const result = res.body;
            if (result['status'] === 'ok') {
                const account: User = result['value'];
                if (account) {
                    this.currentAccount = account;
                    this.authenticated = true;
                } else {
                    this.currentAccount = null;
                    this.authenticated = false;
                }
                return this.currentAccount;
            } else {
                this.currentAccount = null;
                this.authenticated = false;
                return null;
            }
        }).catch((err) => {
            this.currentAccount = null;
            this.authenticated = false;
            return null;
        });
    }

}
