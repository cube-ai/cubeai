import {Routes} from '@angular/router';
import { IdiomComponent } from './idiom.component';

export const idiomRoutes: Routes = [{
    path: 'idiom_solitaire',
    component: IdiomComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
