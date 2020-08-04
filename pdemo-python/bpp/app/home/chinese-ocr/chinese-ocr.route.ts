import {Routes} from '@angular/router';
import { ChineseOcrComponent } from './chinese-ocr.component';

export const chineseOcrRoutes: Routes = [{
    path: 'chinese-ocr',
    component: ChineseOcrComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
