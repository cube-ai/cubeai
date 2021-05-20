import {Routes} from '@angular/router';
import {UserRouteAccessService} from '../../shared';
import {TaskComponent} from './task.component';

export const taskRoutes: Routes = [
    {
        path: 'task',
        component: TaskComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方',
            authorities: ['ROLE_USER'],
        },
        canActivate: [UserRouteAccessService],
    },
];
