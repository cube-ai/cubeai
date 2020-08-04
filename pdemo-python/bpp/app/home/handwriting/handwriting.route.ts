import {Routes} from '@angular/router';
import { HandwritingComponent } from './handwriting.component';

export const handwritingRoutes: Routes = [{
    path: 'handwriting',
    component: HandwritingComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
