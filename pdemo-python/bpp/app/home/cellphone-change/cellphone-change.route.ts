import {Routes} from '@angular/router';
import { CellphoneChangeComponent } from './cellphone-change.component';

export const cellphoneChangeRoutes: Routes = [{
    path: 'cellphone-change',
    component: CellphoneChangeComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
