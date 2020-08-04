import {Routes} from '@angular/router';
import { IdcardNumberComponent } from './idcard-number.component';

export const idcardNumberRoutes: Routes = [{
    path: 'idcardnumber',
    component: IdcardNumberComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
