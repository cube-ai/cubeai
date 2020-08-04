import {Routes} from '@angular/router';
import {LoginComponent} from './login.component';

export const loginRoutes: Routes = [
    {
        path: 'login',
        component: LoginComponent,
        data: {
            pageTitle: '登录'
        }
    },
    {
        path: 'login/:redirectUrl',
        component: LoginComponent,
        data: {
            pageTitle: '登录'
        }
    },
    {
        path: 'login/:redirectUrl/:reason',
        component: LoginComponent,
        data: {
            pageTitle: '登录'
        }
    },
];
