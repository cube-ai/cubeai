import { Routes } from '@angular/router';

import { PackagingComponent } from './packaging.component';
import {UserRouteAccessService} from '../../shared';

export const packagingRoutes: Routes = [
    {
        path: 'packaging',
        component: PackagingComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: '模型打包'
        },
        canActivate: [UserRouteAccessService],
    },
];
