import {Routes} from '@angular/router';
import {PasswordResetComponent} from './password-reset.component';

export const passwordresetRoutes: Routes = [
    {
        path: 'passwordreset',
        component: PasswordResetComponent,
        data: {
            pageTitle: '忘记密码'
        }
    },
    {
        path: 'passwordreset/:resetKey',
        component: PasswordResetComponent,
        data: {
            pageTitle: '重置密码'
        }
    },
];
