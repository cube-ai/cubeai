import {Routes} from '@angular/router';
import {ApproveComponent} from './approve.component';
import {UserRouteAccessService} from '../../shared';

export const approveRoutes: Routes = [
    {
        path: 'approve',
        component: ApproveComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: '模型审批'
        },
        canActivate: [UserRouteAccessService],
    },
];
