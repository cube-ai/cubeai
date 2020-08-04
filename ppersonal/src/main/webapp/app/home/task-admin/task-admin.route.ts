import {Routes} from '@angular/router';
import {UserRouteAccessService} from '../../shared';
import {TaskAdminComponent} from './task-admin.component';

export const taskAdminRoutes: Routes = [
    {
        path: 'taskadmin',
        component: TaskAdminComponent,
        data: {
            pageTitle: '任务管理',
            authorities: ['ROLE_ADMIN'],
        },
        canActivate: [UserRouteAccessService],
    },
];
