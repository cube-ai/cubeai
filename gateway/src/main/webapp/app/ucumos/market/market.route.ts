import {Routes} from '@angular/router';
import {MarketComponent} from './market.component';
import {UserRouteAccessService} from '../../shared';

export const marketRoutes: Routes = [
    {
        path: 'market',
        component: MarketComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: '模型超市'
        },
        canActivate: [UserRouteAccessService],
    },
];
