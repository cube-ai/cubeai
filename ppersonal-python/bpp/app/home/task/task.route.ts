import {Routes} from '@angular/router';
import {UserRouteAccessService} from '../../shared';
import {TaskComponent} from './task.component';

export const taskRoutes: Routes = [
    {
        path: 'task',
        component: TaskComponent,
        data: {
            pageTitle: '个人中心',
            authorities: ['ROLE_USER'],
        },
        canActivate: [UserRouteAccessService],
    },
];
