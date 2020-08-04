import {Routes} from '@angular/router';
import {ActivateComponent} from './activate.component';

export const activateRoutes: Routes = [
    {
        path: 'activate/:activateKey',
        component: ActivateComponent,
        data: {
            pageTitle: '用户帐号激活'
        }
    },
];
