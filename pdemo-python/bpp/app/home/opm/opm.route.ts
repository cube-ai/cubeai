import {Routes} from '@angular/router';
import { OpmComponent } from './opm.component';

export const opmRoutes: Routes = [{
    path: 'opm',
    component: OpmComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
