import {Routes} from '@angular/router';
import {HomeComponent, AppNavigateComponent} from './';

export const homeRoutes: Routes = [
    {
        path: '',
        component: HomeComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方'
        },
    },
    {
        path: 'app/:navTitle',
        component: AppNavigateComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方'
        },
    },
];
