import {Routes} from '@angular/router';
import { ImageColorizerComponent } from './image-colorizer.component';

export const imageColorizerRoutes: Routes = [{
    path: 'imagecolorizer',
    component: ImageColorizerComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
