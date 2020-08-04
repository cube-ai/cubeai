import {Routes} from '@angular/router';
import { TaocanComponent } from './taocan.component';

export const taocanRoutes: Routes = [{
    path: 'taocan',
    component: TaocanComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
