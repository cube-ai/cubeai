import {Routes} from '@angular/router';
import { AntennaMeasurementComponent } from './antenna-measurement.component';

export const antennaMeasurementRoutes: Routes = [{
    path: 'antennaMeasurement',
    component: AntennaMeasurementComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
