import {Routes} from '@angular/router';
import { ArtWordComponent } from './art-word.component';

export const artWordRoutes: Routes = [{
    path: 'art_word',
    component: ArtWordComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
