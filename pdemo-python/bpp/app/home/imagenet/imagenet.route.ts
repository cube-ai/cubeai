import {Routes} from '@angular/router';
import { ImagenetComponent } from './imagenet.component';

export const imagenetRoutes: Routes = [{
    path: 'imagenet',
    component: ImagenetComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
