import {Routes} from '@angular/router';

import {
    ucumosHomeRoutes,
    marketRoutes,
    personalRoutes,
    ochestratorRoutes,
    onboardingRoutes,
    approveRoutes,
    favoriteRoutes,
    sharedRoutes,
    taskRoutes,
    solutionRoutes,
    deployRoutes,
    packagingRoutes,
} from './';

import { UserRouteAccessService } from '../shared';
import { UcumosComponent } from './';

export const ucumosRoutes: Routes = [{
    path: 'ucumos',
    component: UcumosComponent,
    data: {
        authorities: [],
    },
    canActivate: [UserRouteAccessService],
    children: [
        ...ucumosHomeRoutes,
        ...marketRoutes,
        ...personalRoutes,
        ...ochestratorRoutes,
        ...onboardingRoutes,
        ...approveRoutes,
        ...favoriteRoutes,
        ...sharedRoutes,
        ...taskRoutes,
        ...solutionRoutes,
        ...deployRoutes,
        ...packagingRoutes,
    ]
},
];
