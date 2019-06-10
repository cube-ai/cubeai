import { Routes } from '@angular/router';

import { AiAbilityHomeComponent } from './ai-ability-home.component';
import {UserRouteAccessService} from '../../shared';

export const aiAbilityHomeRoutes: Routes = [
    {
        path: '',
        component: AiAbilityHomeComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'AI能力开放'
        },
        canActivate: [UserRouteAccessService],
    },
    {
        path: 'home',
        component: AiAbilityHomeComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'AI能力开放'
        },
        canActivate: [UserRouteAccessService],
    },
];
