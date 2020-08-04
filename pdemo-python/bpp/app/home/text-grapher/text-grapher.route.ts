import {Routes} from '@angular/router';
import { TextGrapherComponent } from './text-grapher.component';

export const textGrapherRoutes: Routes = [{
    path: 'text_grapher',
    component: TextGrapherComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
