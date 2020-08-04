import {Routes} from '@angular/router';
import { ExchangerateComponent } from './exchangerate.component';

export const exchangerateRoutes: Routes = [{
    path: 'exchangerate',
    component: ExchangerateComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
