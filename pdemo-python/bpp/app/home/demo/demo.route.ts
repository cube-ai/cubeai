import {Routes} from '@angular/router';
import { DemoComponent } from './demo.component';

export const demoRoutes: Routes = [{
    path: 'demo',
    component: DemoComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
