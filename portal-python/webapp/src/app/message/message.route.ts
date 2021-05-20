import {Routes} from '@angular/router';
import { UserRouteAccessService } from '../shared';
import {MessageComponent} from './message.component';
import {inboxRoutes} from './inbox/inbox.route';
import {sentRoutes} from './sent/sent.route';
import {deletedRoutes} from './deleted/deleted.route';

export const messageRoutes: Routes = [{
    path: 'message',
    component: MessageComponent,
    data: {
        authorities: ['ROLE_USER'],
    },
    canActivate: [UserRouteAccessService],
    children: [
        ...inboxRoutes,
        ...sentRoutes,
        ...deletedRoutes,
    ]
},
];
