import {Routes} from '@angular/router';
import { TraintableComponent } from './traintable.component';

export const traintableRoutes: Routes = [{
    path: 'traintable',
    component: TraintableComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
