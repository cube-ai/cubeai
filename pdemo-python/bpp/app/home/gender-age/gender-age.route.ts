import {Routes} from '@angular/router';
import { GenderAgeComponent } from './gender-age.component';

export const genderAgeRoutes: Routes = [{
    path: 'gender_age_estimate',
    component: GenderAgeComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
