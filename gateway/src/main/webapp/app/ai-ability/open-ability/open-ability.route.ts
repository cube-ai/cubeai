import {Routes} from '@angular/router';
import {OpenAbilityComponent} from './open-ability.component';
import {UserRouteAccessService} from '../../shared';

export const openAbilityRoutes: Routes = [
    {
        path: 'open-ability',
        component: OpenAbilityComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: '能力开放'
        },
        canActivate: [UserRouteAccessService],
    },
];
