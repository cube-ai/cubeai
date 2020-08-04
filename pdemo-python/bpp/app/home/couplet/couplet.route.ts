import {Routes} from '@angular/router';
import { CoupletComponent } from './couplet.component';

export const coupletRoutes: Routes = [{
    path: 'couplet',
    component: CoupletComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
