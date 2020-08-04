import {Routes} from '@angular/router';
import { SpeechActComponent } from './speech-act.component';

export const speechActRoutes: Routes = [{
    path: 'speechact',
    component: SpeechActComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
