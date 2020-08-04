import {Routes} from '@angular/router';
import { FacialEmotionComponent } from './facial-emotion.component';

export const facialEmotionRoutes: Routes = [{
    path: 'facialemotion',
    component: FacialEmotionComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
