import {Routes} from '@angular/router';
import { CarPlateComponent } from './car-plate.component';

export const carPlateRoutes: Routes = [{
    path: 'carplate',
    component: CarPlateComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
