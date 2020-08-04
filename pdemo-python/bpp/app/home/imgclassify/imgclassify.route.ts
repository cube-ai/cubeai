import {Routes} from '@angular/router';
import { ImgclassifyComponent } from './imgclassify.component';

export const imgclassifyRoutes: Routes = [{
    path: 'imgclassify',
    component: ImgclassifyComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
