import { Injectable } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { Principal } from './principal.service';
import { MatDialog } from '@angular/material';
import {SnackBarService} from './snackbar.service';

@Injectable()
export class UserRouteAccessService implements CanActivate {

    constructor(private router: Router,
                private location: Location,
                public dialog: MatDialog,
                private principal: Principal,
                private snackBarService: SnackBarService,
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
                    this.snackBarService.error('你没有权限访问该应用！');
                } else {
                    const reason = '该应用需要登录才能访问，请登录...';
                    const redirectUrl = (window.location.pathname + '@' + currentUrl).replace(/\//g, '$');
                    window.location.href = '/#/login/' + redirectUrl + '/' + reason;
                }
            }
            return canActivate;
        }));
    }
}
