import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import {homeRoute, errorRoutes, homeArticleRoute} from './layouts';
import { DEBUG_INFO_ENABLED } from './app.constants';

const LAYOUT_ROUTES = [
    ...errorRoutes,
    homeRoute,
    homeArticleRoute,
];

@NgModule({
    imports: [
        RouterModule.forRoot(LAYOUT_ROUTES, { useHash: true , enableTracing: DEBUG_INFO_ENABLED })
    ],
    exports: [
        RouterModule
    ]
})
export class GatewayAppRoutingModule {}
