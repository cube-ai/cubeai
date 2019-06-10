import {Routes} from '@angular/router';

import {
    aiAbilityHomeRoutes,
    openAbilityRoutes,
    myAbilityRoutes,
    allAbilityRoutes,
    abilityRoutes,
} from './';

import { UserRouteAccessService } from '../shared';
import { AiAbilityComponent } from './';

export const aiAbilityRoutes: Routes = [{
    path: 'ai-ability',
    component: AiAbilityComponent,
    data: {
        authorities: [],
    },
    canActivate: [UserRouteAccessService],
    children: [
        ...aiAbilityHomeRoutes,
        ...openAbilityRoutes,
        ...myAbilityRoutes,
        ...allAbilityRoutes,
        ...abilityRoutes,
    ]
},
];
