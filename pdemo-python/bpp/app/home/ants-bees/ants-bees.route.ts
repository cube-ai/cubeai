import {Routes} from '@angular/router';
import {AntsBeesComponent} from './ants-bees.component';

export const antsBeesRoutes: Routes = [{
    path: 'ants-bees',
    component: AntsBeesComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
