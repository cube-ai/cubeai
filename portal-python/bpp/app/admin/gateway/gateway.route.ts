import { Route } from '@angular/router';

import { GatewayComponent } from './gateway.component';
import {UserRouteAccessService} from '../../shared';

export const gatewayRoute: Route = {
    path: 'gateway',
    component: GatewayComponent,
    data: {
        pageTitle: '路由网关',
        authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
};
