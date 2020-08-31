import {Routes} from '@angular/router';
import {SentComponent} from './sent.component';
import {UserRouteAccessService} from '../../shared';

export const sentRoutes: Routes = [
    {
        path: 'msg-sent',
        component: SentComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: '已发送',
        },
        canActivate: [UserRouteAccessService],
    },
];
