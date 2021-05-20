import {Routes} from '@angular/router';
import {PasswordResetComponent} from './password-reset.component';

export const passwordresetRoutes: Routes = [
    {
        path: 'passwordreset',
        component: PasswordResetComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方'
        }
    },
    {
        path: 'passwordreset/:resetKey',
        component: PasswordResetComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方'
        }
    },
];
