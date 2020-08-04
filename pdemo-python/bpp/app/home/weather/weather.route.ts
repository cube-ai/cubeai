import {Routes} from '@angular/router';
import { WeatherComponent } from './weather.component';

export const weatherRoutes: Routes = [{
    path: 'weather',
    component: WeatherComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
