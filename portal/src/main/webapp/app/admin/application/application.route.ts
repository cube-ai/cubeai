import {Routes} from '@angular/router';
import {ApplicationComponent} from './application.component';
import {AppdetailComponent} from './appdetail.component';
import {UserRouteAccessService} from '../../shared';

export const applicationRoutes: Routes = [
    {
        path: 'application',
        component: ApplicationComponent,
        data: {
            authorities: ['ROLE_APPLICATION'],
            pageTitle: '应用管理'
        },
        canActivate: [UserRouteAccessService],
    },
    {
        path: 'appdetail/:mode/:id',
        component: AppdetailComponent,
        data: {
            authorities: ['ROLE_APPLICATION'],
            pageTitle: '应用'
        },
        canActivate: [UserRouteAccessService],
    },
];
