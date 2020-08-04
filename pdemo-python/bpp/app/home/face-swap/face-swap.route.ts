import {Routes} from '@angular/router';
import { FaceSwapComponent } from './face-swap.component';

export const faceSwapRoutes: Routes = [{
    path: 'faceswap',
    component: FaceSwapComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
