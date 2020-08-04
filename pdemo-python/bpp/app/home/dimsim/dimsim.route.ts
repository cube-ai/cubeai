import {Routes} from '@angular/router';
import { DimsimComponent } from './dimsim.component';

export const dimsimRoutes: Routes = [{
    path: 'dimsim',
    component: DimsimComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
