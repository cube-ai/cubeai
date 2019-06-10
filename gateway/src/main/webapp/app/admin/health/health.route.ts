import { Route } from '@angular/router';

import { JhiHealthCheckComponent } from './health.component';
import {UserRouteAccessService} from '../../shared';

export const healthRoute: Route = {
    path: 'health',
    component: JhiHealthCheckComponent,
    data: {
        pageTitle: '健康测量',
        authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
};
