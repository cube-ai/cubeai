import {Routes} from '@angular/router';

import {
    adminHomeRoutes,
    configurationRoute,
    docsRoute,
    healthRoute,
    metricsRoute,
    gatewayRoute,
    userMgmtRoute,
    bulletinRoutes,
    articleRoutes,
} from './';

import { UserRouteAccessService } from '../shared';
import {AdminComponent} from './';

export const adminRoutes: Routes = [{
    path: 'admin',
    component: AdminComponent,
    data: {
        authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
    children: [
        ...adminHomeRoutes,
        configurationRoute,
        docsRoute,
        healthRoute,
        gatewayRoute,
        ...userMgmtRoute,
        ...bulletinRoutes,
        ...articleRoutes,
        metricsRoute
    ]
},
];
