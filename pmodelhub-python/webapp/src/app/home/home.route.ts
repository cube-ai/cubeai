import {Route, Routes} from '@angular/router';
import {HomeComponent} from './home.component';
import {marketRoutes} from './market/market.route';
import {personalRoutes} from './personal/personal.route';
import {stargazerRoutes} from './stargazer/stargazer.route';
import {solutionRoutes} from './solution/solution.route';
import {onboardingRoutes} from './onboarding/onboarding.route';
import {packagingRoutes} from './packaging/packaging.route';
import {deployRoutes} from './deploy/deploy.route';

const homeRoute: Route = {
    path: '',
    component: HomeComponent,
    data: {
        pageTitle: 'CubeAI ★ 智立方',
    },
};

export const homeRoutes: Routes = [
    homeRoute,
    ...marketRoutes,
    ...personalRoutes,
    ...stargazerRoutes,
    ...solutionRoutes,
    ...onboardingRoutes,
    ...packagingRoutes,
    ...deployRoutes,
];
