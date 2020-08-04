import {Routes} from '@angular/router';

import {
    gatewayRoute,
    userMgmtRoute,
    roleMgmtRoute,
    bulletinRoutes,
    articleRoutes,
    attachmentRoutes,
    applicationRoutes,
} from './';
import { UserRouteAccessService } from '../shared';
import {AdminComponent} from './';

export const adminRoutes: Routes = [{
    path: 'admin',
    component: AdminComponent,
    data: {
        authorities: ['ROLE_ADMIN', 'ROLE_CONTENT', 'ROLE_APPLICATION'],
    },
    canActivate: [UserRouteAccessService],
    children: [
        gatewayRoute,
        ...userMgmtRoute,
        ...roleMgmtRoute,
        ...bulletinRoutes,
        ...articleRoutes,
        ...attachmentRoutes,
        ...applicationRoutes,
    ]
},
];
