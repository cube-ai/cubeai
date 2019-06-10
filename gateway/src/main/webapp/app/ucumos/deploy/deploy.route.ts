import {Routes} from '@angular/router';
import {DeployComponent} from './deploy.component';
import {UserRouteAccessService} from '../../shared';

export const deployRoutes: Routes = [
    {
        path: 'deploy/:openMode/:uuid',
        component: DeployComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: '模型部署'
        },
        canActivate: [UserRouteAccessService],
    },
];
