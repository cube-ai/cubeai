import {Routes} from '@angular/router';
import {CreditComponent} from './credit.component';
import {UserRouteAccessService} from '../../shared';

export const creditRoutes: Routes = [
    {
        path: 'credit',
        component: CreditComponent,
        data: {
            pageTitle: '个人中心',
            authorities: ['ROLE_USER'],
        },
        canActivate: [UserRouteAccessService],
    },
];
