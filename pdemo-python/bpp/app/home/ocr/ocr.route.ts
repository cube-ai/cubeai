import {Routes} from '@angular/router';
import { OcrComponent } from './ocr.component';

export const ocrRoutes: Routes = [{
    path: 'ocr',
    component: OcrComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
