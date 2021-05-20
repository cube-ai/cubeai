import {Routes} from '@angular/router';
import {MarketComponent} from './market.component';

export const marketRoutes: Routes = [
    {
        path: 'market',
        component: MarketComponent,
        data: {
            authorities: [],
            pageTitle: 'CubeAI ★ 智立方',
        },
    },
];
