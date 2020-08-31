import {Routes} from '@angular/router';
import { UserRouteAccessService } from '../shared';
import { MessageComponent } from './';
import {
    messageHomeRoutes,
    inboxRoutes,
    deletedRoutes,
    sentRoutes,
} from './';

export const messageRoutes: Routes = [{
    path: 'message',
    component: MessageComponent,
    data: {
        authorities: ['ROLE_USER'],
    },
    canActivate: [UserRouteAccessService],
    children: [
        ...messageHomeRoutes,
        ...inboxRoutes,
        ...deletedRoutes,
        ...sentRoutes,
    ]
},
];
