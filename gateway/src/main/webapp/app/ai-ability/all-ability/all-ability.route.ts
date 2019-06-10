import {Routes} from '@angular/router';
import {AllAbilityComponent} from './all-ability.component';
import {UserRouteAccessService} from '../../shared';

export const allAbilityRoutes: Routes = [
    {
        path: 'all-ability',
        component: AllAbilityComponent,
        data: {
            authorities: ['ROLE_OPERATOR'],
            pageTitle: '能力监控'
        },
        canActivate: [UserRouteAccessService],
    },
];
