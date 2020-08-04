import {Routes} from '@angular/router';
import { IdcardInfoComponent } from './idcard-info.component';

export const idcardInfoRoutes: Routes = [{
    path: 'idcardinfo',
    component: IdcardInfoComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
