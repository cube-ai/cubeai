import {Routes} from '@angular/router';
import {LoginComponent} from './login.component';

export const loginRoutes: Routes = [
    {
        path: 'login',
        component: LoginComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方'
        }
    },
];
