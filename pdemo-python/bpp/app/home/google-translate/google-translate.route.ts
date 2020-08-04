import {Routes} from '@angular/router';
import { GoogleTranslateComponent } from './google-translate.component';

export const googleTranslateRoutes: Routes = [{
    path: 'google-translate',
    component: GoogleTranslateComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
