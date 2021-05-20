import {Routes} from '@angular/router';
import {RegisterComponent} from './register.component';

export const registerRoutes: Routes = [
    {
        path: 'register',
        component: RegisterComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方'
        }
    },
];
