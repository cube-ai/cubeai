import { Routes } from '@angular/router';
import { RoleMgmtComponent } from './role-management.component';
import {UserRouteAccessService} from '../../shared';

export const roleMgmtRoute: Routes = [
    {
        path: 'role-management',
        component: RoleMgmtComponent,
        data: {
            pageTitle: '角色管理',
            authorities: ['ROLE_ADMIN'],
        },
        canActivate: [UserRouteAccessService],
    },
];
