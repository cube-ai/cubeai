import {Routes} from '@angular/router';
import { VehicleComponent } from './vehicle.component';

export const vehicleRoutes: Routes = [{
    path: 'vehicle',
    component: VehicleComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
