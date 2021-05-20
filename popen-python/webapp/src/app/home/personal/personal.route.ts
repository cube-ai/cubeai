import {Routes} from '@angular/router';
import {PersonalComponent} from './personal.component';

export const personalRoutes: Routes = [
    {
        path: 'personal/:deployer',
        component: PersonalComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方',
        },
    },
];
