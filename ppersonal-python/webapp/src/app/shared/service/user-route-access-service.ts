import { Injectable } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { Principal } from './principal.service';
import {MessageService} from 'primeng/api';

@Injectable()
export class UserRouteAccessService implements CanActivate {

    constructor(private router: Router,
                private location: Location,
                private principal: Principal,
                private messageService: MessageService,
    ) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | Promise<boolean> {
        return this.checkLogin(route.data['authorities'], state.url);
    }

    checkLogin(authorities: string[], currentUrl: string): Promise<boolean> {
        return Promise.resolve( this.principal.updateCurrentAccount().then(() => {
            const canActivate = this.principal.hasAnyAuthority(authorities);
            if (!canActivate) {
                if (this.principal.isAuthenticated()) {
                    this.messageService.add({severity:'warn', detail:'你没有权限访问该应用！'});
                } else {
                    window.localStorage.setItem('loginRedirectUrl', window.location.pathname + '#' + currentUrl);
                    window.localStorage.setItem('loginReason', '该应用需要登录才能访问，请登录...');
                    window.location.href = '/#/login';
                }
            }
            return canActivate;
        }));
    }
}
