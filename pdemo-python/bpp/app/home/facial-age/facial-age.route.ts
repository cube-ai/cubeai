import {Routes} from '@angular/router';
import { FacialAgeComponent } from './facial-age.component';

export const facialAgeRoutes: Routes = [{
    path: 'facialage',
    component: FacialAgeComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
