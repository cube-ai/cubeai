import {Routes} from '@angular/router';
import {OchestratorComponent} from './ochestrator.component';
import {UserRouteAccessService} from '../../shared';

export const ochestratorRoutes: Routes = [
    {
        path: 'ochestrator',
        component: OchestratorComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: '模型编排'
        },
        canActivate: [UserRouteAccessService],
    },
];
