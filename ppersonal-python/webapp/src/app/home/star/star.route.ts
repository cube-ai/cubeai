import {Routes} from '@angular/router';
import {StarComponent} from './star.component';

export const starRoutes: Routes = [
    {
        path: 'star/:starerLogin',
        component: StarComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方',
        },
    },
];
