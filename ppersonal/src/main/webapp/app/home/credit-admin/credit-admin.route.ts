import {Routes} from '@angular/router';
import {CreditAdminComponent} from './credit-admin.component';
import {CreditDetailComponent} from './credit-detail.component';
import {UserRouteAccessService} from '../../shared';

export const creditAdminRoutes: Routes = [
    {
        path: 'creditadmin',
        component: CreditAdminComponent,
        data: {
            pageTitle: '积分管理',
            authorities: ['ROLE_ADMIN'],
        },
        canActivate: [UserRouteAccessService],
    },
    {
        path: 'creditdetail/:userLogin',
        component: CreditDetailComponent,
        data: {
            pageTitle: '积分管理',
            authorities: ['ROLE_ADMIN'],
        },
        canActivate: [UserRouteAccessService],
    },
];
