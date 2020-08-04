import {Route, Routes} from '@angular/router';
import {HomeComponent} from './home.component';
import {
    marketRoutes,
    personalRoutes,
    abilityRoutes,
    stargazerRoutes,
} from './';

const homeRoute: Route = {
    path: '',
    component: HomeComponent,
    data: {
        pageTitle: 'CubeAI能力开放平台',
    },
};

export const homeRoutes: Routes = [
    homeRoute,
    ...marketRoutes,
    ...personalRoutes,
    ...abilityRoutes,
    ...stargazerRoutes,
];
