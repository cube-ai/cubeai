import {Routes} from '@angular/router';
import {OcrWordCloudComponent} from './ocrwordcloud.component';

export const ocrWordcloudRoutes: Routes = [{
    path: 'ocrwordcloud',
    component: OcrWordCloudComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
