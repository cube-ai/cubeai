import {Routes} from '@angular/router';
import { PinyinComponent } from './pinyin.component';

export const pinyinRoutes: Routes = [{
    path: 'pinyin',
    component: PinyinComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
