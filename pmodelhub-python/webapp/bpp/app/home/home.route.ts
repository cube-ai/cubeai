import {Route, Routes} from '@angular/router';
import {HomeComponent} from './home.component';
import {
    solutionRoutes,
    marketRoutes,
    personalRoutes,
    onboardingRoutes,
    packagingRoutes,
    deployRoutes,
    stargazerRoutes,
} from './';

const homeRoute: Route = {
    path: '',
    component: HomeComponent,
    data: {
        pageTitle: 'CubeAI模型共享',
    },
};

export const homeRoutes: Routes = [
    homeRoute,
    ...solutionRoutes,
    ...marketRoutes,
    ...personalRoutes,
    ...onboardingRoutes,
    ...packagingRoutes,
    ...deployRoutes,
    ...stargazerRoutes,
];
