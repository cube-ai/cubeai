import {Routes} from '@angular/router';
import {SharedComponent} from './shared.component';
import {UserRouteAccessService} from '../../shared';

export const sharedRoutes: Routes = [
    {
        path: 'shared',
        component: SharedComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: '我的收藏'
        },
        canActivate: [UserRouteAccessService],
    },
];
