import {Routes} from '@angular/router';

import {
    messageHomeRoutes,
    inboxRoutes,
    deletedRoutes,
    sentRoutes,
} from './';

import { UserRouteAccessService } from '../shared';
import { MessageComponent } from './';

export const messageRoutes: Routes = [{
    path: 'message',
    component: MessageComponent,
    data: {
        authorities: [],
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
