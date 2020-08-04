import {Routes} from '@angular/router';
import {RegisterComponent} from './register.component';

export const registerRoutes: Routes = [
    {
        path: 'register',
        component: RegisterComponent,
        data: {
            pageTitle: '注册新用户'
        }
    },
];
