import {Routes} from '@angular/router';

import {UserRouteAccessService} from '../shared';
import {AdminComponent} from './admin.component';
import {roleMgmtRoute} from './role-management/role-management.route';
import {userMgmtRoute} from './user-management/user-management.route';
import {applicationRoutes} from './application/application.route';
import {articlesRoutes} from './articles/articles.route';
import {attachmentRoutes} from './attachment/attachment.route';

export const adminRoutes: Routes = [{
    path: 'admin',
    component: AdminComponent,
    data: {
        authorities: ['ROLE_ADMIN', 'ROLE_CONTENT', 'ROLE_APPLICATION'],
    },
    canActivate: [UserRouteAccessService],
    children: [
        ...roleMgmtRoute,
        ...userMgmtRoute,
        ...applicationRoutes,
        ...articlesRoutes,
        ...attachmentRoutes,
    ]
},
];
