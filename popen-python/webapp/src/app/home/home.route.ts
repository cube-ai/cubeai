import {Route, Routes} from '@angular/router';
import {HomeComponent} from './home.component';
import {marketRoutes} from './market/market.route';
import {personalRoutes} from './personal/personal.route';
import {stargazerRoutes} from './stargazer/stargazer.route';
import {demoRoutes} from './demo/demo.route';
import {abilityRoutes} from './ability/ability.route';

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
    ...demoRoutes,
    ...abilityRoutes,
];
