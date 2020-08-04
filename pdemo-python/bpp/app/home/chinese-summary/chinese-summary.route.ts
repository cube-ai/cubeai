import {Routes} from '@angular/router';
import { ChineseSummaryComponent } from './chinese-summary.component';

export const chineseSummaryRoutes: Routes = [{
    path: 'chinese-sammary',
    component: ChineseSummaryComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
