import {Route, Routes} from '@angular/router';
import {UserRouteAccessService} from '../shared';
import {HomeComponent} from './home.component';
import {
    starRoutes,
    taskRoutes,
    taskAdminRoutes,
    creditRoutes,
    creditAdminRoutes,
} from './';

const homeRoute: Route = {
    path: '',
    component: HomeComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'CubeAI个人中心',
    },
    canActivate: [UserRouteAccessService],
};

export const homeRoutes: Routes = [
    homeRoute,
    ...starRoutes,
    ...taskRoutes,
    ...taskAdminRoutes,
    ...creditRoutes,
    ...creditAdminRoutes,
];
