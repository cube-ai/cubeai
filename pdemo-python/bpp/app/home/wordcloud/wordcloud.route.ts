import {Routes} from '@angular/router';
import { WordcloudComponent } from './wordcloud.component';

export const wordcloudRoutes: Routes = [
    {
        path: 'wordcloud',
        component: WordcloudComponent,
        data: {
            pageTitle: 'CubeAI开放能力演示'
        },
    },
];
