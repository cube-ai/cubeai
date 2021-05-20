import {Route, Routes} from '@angular/router';
import {UserRouteAccessService} from '../shared';
import {HomeComponent} from './home.component';
import {starRoutes} from './star/star.route';
import {taskRoutes} from './task/task.route';
import {creditRoutes} from './credit/credit.route';
import {taskAdminRoutes} from './task-admin/task-admin.route';
import {creditAdminRoutes} from './credit-admin/credit-admin.route';

const homeRoute: Route = {
    path: '',
    component: HomeComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'CubeAI ★ 智立方',
    },
    canActivate: [UserRouteAccessService],
};

export const homeRoutes: Routes = [
    homeRoute,
    ...starRoutes,
    ...taskRoutes,
    ...creditRoutes,
    ...taskAdminRoutes,
    ...creditAdminRoutes,
];
