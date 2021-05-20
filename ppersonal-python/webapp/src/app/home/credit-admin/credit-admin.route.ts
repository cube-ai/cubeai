import {Routes} from '@angular/router';
import {UserRouteAccessService} from '../../shared';
import {CreditAdminComponent} from './credit-admin.component';
import {CreditDetailComponent} from './credit-detail.component';

export const creditAdminRoutes: Routes = [
    {
        path: 'creditadmin',
        component: CreditAdminComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方',
            authorities: ['ROLE_ADMIN'],
        },
        canActivate: [UserRouteAccessService],
    },
    {
        path: 'creditdetail/:userLogin',
        component: CreditDetailComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方',
            authorities: ['ROLE_ADMIN'],
        },
        canActivate: [UserRouteAccessService],
    },
];
