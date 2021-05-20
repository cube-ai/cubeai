import {Routes} from '@angular/router';
import {UserRouteAccessService} from '../../shared';
import {TaskAdminComponent} from './task-admin.component';

export const taskAdminRoutes: Routes = [
    {
        path: 'taskadmin',
        component: TaskAdminComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方',
            authorities: ['ROLE_ADMIN'],
        },
        canActivate: [UserRouteAccessService],
    },
];
