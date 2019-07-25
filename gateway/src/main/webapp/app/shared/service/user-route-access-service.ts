import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { Principal } from '../../account';
import { MatDialog } from '@angular/material';
import {LoginComponent} from '../../account';
import {SnackBarService} from './snackbar.service';

@Injectable()
export class UserRouteAccessService implements CanActivate {

    constructor(private router: Router,
                public dialog: MatDialog,
                private principal: Principal,
                private snackBarService: SnackBarService,
                ) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | Promise<boolean> {
        const authorities = route.data['authorities'];

        // We need to call the checkLogin / and so the principal.identity() function, to ensure,
        // that the client has a principal too, if they already logged in by the server.
        // This could happen on a page refresh.
        return this.checkLogin(authorities, state.url);
    }

    checkLogin(authorities: string[], url: string): Promise<boolean> {
        return Promise.resolve( this.principal.updateCurrentAccount().then((account) => {
            if (account) {
                if (!authorities || authorities.length === 0) {
                    return true;
                }
                const canActivate = this.principal.hasAnyAuthorityDirect(authorities);
                if (!canActivate) {
                    this.snackBarService.error('你没有权限访问该模块！');
                }
                return canActivate;
            } else {
                const config = {
                    width: '600px',
                    data: {
                        reason: '登录超时，请重新登录......',
                        redirectUrl: url,
                    }
                };
                if (window.screen.height < 800) {
                    config['height'] = '600px';
                }
                this.dialog.open(LoginComponent, config);
                return false;
            }
        }));
    }
}
