import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DEBUG_INFO_ENABLED } from './app.constants';
import {layoutsRoutes} from './layouts';
import {homeRoutes} from './home';
import {accountRoutes} from './account';

const ROOT_ROUTES = [
    ...layoutsRoutes,
    ...homeRoutes,
    ...accountRoutes,
];

@NgModule({
    imports: [
        RouterModule.forRoot(ROOT_ROUTES, { useHash: true , enableTracing: DEBUG_INFO_ENABLED })
    ],
    exports: [
        RouterModule
    ]
})
export class AppMainRoutingModule {}
