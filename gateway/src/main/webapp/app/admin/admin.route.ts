import {Routes} from '@angular/router';

import {
    adminHomeRoutes,
    configurationRoute,
    docsRoute,
    healthRoute,
    metricsRoute,
    gatewayRoute,
    userMgmtRoute,
    roleMgmtRoute,
    bulletinRoutes,
    articleRoutes,
    attachmentRoutes,
} from './';

import { UserRouteAccessService } from '../shared';
import {AdminComponent} from './';

export const adminRoutes: Routes = [{
    path: 'admin',
    component: AdminComponent,
    data: {
        authorities: ['ROLE_ADMIN', 'ROLE_CONTENT'],
    },
    canActivate: [UserRouteAccessService],
    children: [
        ...adminHomeRoutes,
        configurationRoute,
        docsRoute,
        healthRoute,
        gatewayRoute,
        ...userMgmtRoute,
        ...roleMgmtRoute,
        ...bulletinRoutes,
        ...articleRoutes,
        ...attachmentRoutes,
        metricsRoute
    ]
},
];
