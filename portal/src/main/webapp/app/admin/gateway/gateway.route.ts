import { Route } from '@angular/router';

import { JhiGatewayComponent } from './gateway.component';
import {UserRouteAccessService} from '../../shared';

export const gatewayRoute: Route = {
    path: 'gateway',
    component: JhiGatewayComponent,
    data: {
        pageTitle: '路由网关',
        authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
};
