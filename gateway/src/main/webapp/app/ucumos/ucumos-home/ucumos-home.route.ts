import { Routes } from '@angular/router';

import { UcumosHomeComponent } from './ucumos-home.component';
import {UserRouteAccessService} from '../../shared';

export const ucumosHomeRoutes: Routes = [
    {
        path: '',
        component: UcumosHomeComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'AI商城'
        },
        canActivate: [UserRouteAccessService],
    },
    {
        path: 'home',
        component: UcumosHomeComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'AI商城'
        },
        canActivate: [UserRouteAccessService],
    },
];
