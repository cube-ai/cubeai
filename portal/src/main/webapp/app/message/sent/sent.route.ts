import {Routes} from '@angular/router';
import {SentComponent} from './sent.component';
import {UserRouteAccessService} from '../../shared';

export const sentRoutes: Routes = [
    {
        path: 'msg-sent',
        component: SentComponent,
        data: {
            authorities: [],
            pageTitle: '已发送',
        },
        canActivate: [UserRouteAccessService],
    },
];
