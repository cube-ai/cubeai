import {Routes} from '@angular/router';
import { ImageRectifyComponent } from './image-rectify.component';

export const imageRectifyRoutes: Routes = [{
    path: 'imagerectify',
    component: ImageRectifyComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
