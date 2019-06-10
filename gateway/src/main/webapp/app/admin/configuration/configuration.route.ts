import { Route } from '@angular/router';

import { JhiConfigurationComponent } from './configuration.component';
import {UserRouteAccessService} from '../../shared';

export const configurationRoute: Route = {
    path: 'configuration',
    component: JhiConfigurationComponent,
    data: {
        pageTitle: '应用配置',
        authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
};
