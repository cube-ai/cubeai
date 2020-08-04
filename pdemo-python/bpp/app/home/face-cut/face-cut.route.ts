import {Routes} from '@angular/router';
import { FaceCutComponent } from './face-cut.component';

export const faceCutRoutes: Routes = [{
    path: 'face_cut',
    component: FaceCutComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
