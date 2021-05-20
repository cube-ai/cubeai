import {Routes} from '@angular/router';
import {ActivateComponent} from './activate.component';

export const activateRoutes: Routes = [
    {
        path: 'activate/:activateKey',
        component: ActivateComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方'
        }
    },
];
