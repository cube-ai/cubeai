import {Routes} from '@angular/router';
import { SentimentComponent } from './sentiment.component';

export const sentimentRoutes: Routes = [{
    path: 'sentiment',
    component: SentimentComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
