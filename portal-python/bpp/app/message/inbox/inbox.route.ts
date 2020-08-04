import {Routes} from '@angular/router';
import {InboxComponent} from './inbox.component';
import {UserRouteAccessService} from '../../shared';

export const inboxRoutes: Routes = [
    {
        path: 'msg-inbox',
        component: InboxComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: '收信箱',
        },
        canActivate: [UserRouteAccessService],
    },
];
