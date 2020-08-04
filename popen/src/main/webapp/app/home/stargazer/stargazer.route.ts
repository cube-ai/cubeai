import {Routes} from '@angular/router';
import {StargazerComponent} from './stargazer.component';

export const stargazerRoutes: Routes = [
    {
        path: 'stargazer/:abilityUuid/:abilityName',
        component: StargazerComponent,
        data: {
            pageTitle: '粉丝'
        },
    },
];
