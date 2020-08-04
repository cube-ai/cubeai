import {Routes} from '@angular/router';
import { ImageEnhancerComponent } from './image-enhancer.component';

export const imageEnhancerRoutes: Routes = [{
    path: 'imageupscale',
    component: ImageEnhancerComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
