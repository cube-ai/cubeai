import { Injectable } from '@angular/core';
import { Routes, CanActivate } from '@angular/router';
import { UserMgmtComponent } from './user-management.component';
import {UserRouteAccessService, Principal} from '../../shared';

@Injectable()
export class UserResolve implements CanActivate {

    constructor(private principal: Principal) { }

    canActivate() {
        return this.principal.updateCurrentAccount().then((account) => this.principal.hasAnyAuthority(['ROLE_ADMIN']));
    }
}

export const userMgmtRoute: Routes = [
    {
        path: 'user-management',
        component: UserMgmtComponent,
        data: {
            pageTitle: '用户管理',
            authorities: ['ROLE_ADMIN'],
        },
        canActivate: [UserRouteAccessService],
    },
];
