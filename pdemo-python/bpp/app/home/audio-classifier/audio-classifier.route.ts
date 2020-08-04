import {Routes} from '@angular/router';
import { AudioClassifierComponent } from './audio-classifier.component';

export const audioClassifierRoutes: Routes = [{
    path: 'audio-classifier',
    component: AudioClassifierComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
