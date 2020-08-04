import {Routes} from '@angular/router';
import { FaceRecognitionComponent } from './face-recognition.component';

export const faceRecognitionRoutes: Routes = [{
    path: 'facerecognition',
    component: FaceRecognitionComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
