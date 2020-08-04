import {Routes} from '@angular/router';
import { TextCorrectComponent } from './text-correct.component';

export const textCorrectRoutes: Routes = [{
    path: 'textcorrect',
    component: TextCorrectComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
