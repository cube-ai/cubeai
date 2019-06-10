import { Route } from '@angular/router';

import { JhiMetricsMonitoringComponent } from './metrics.component';
import {UserRouteAccessService} from '../../shared';

export const metricsRoute: Route = {
    path: 'app-metrics',
    component: JhiMetricsMonitoringComponent,
    data: {
        pageTitle: '性能指标',
        authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
};
