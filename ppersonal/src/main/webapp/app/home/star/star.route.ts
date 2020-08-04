import {Routes} from '@angular/router';
import {StarComponent} from './star.component';

export const starRoutes: Routes = [
    {
        path: 'star/:starerLogin',
        component: StarComponent,
        data: {
            pageTitle: '个人中心',
        },
    },
];
