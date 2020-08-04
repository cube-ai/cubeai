import {Routes} from '@angular/router';
import { FaceRecognizerComponent } from './face-recognizer.component';

export const faceRecognizerRoutes: Routes = [{
    path: 'facerecognizer',
    component: FaceRecognizerComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
