import {Routes} from '@angular/router';
import {MyAbilityComponent} from './my-ability.component';
import {UserRouteAccessService} from '../../shared';

export const myAbilityRoutes: Routes = [
    {
        path: 'my-ability',
        component: MyAbilityComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: '能力开放'
        },
        canActivate: [UserRouteAccessService],
    },
];
