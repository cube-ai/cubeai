import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DEBUG_INFO_ENABLED } from './app.constants';
import {layoutsRoutes} from './layouts';
import { homeRoutes } from './home';

const ROOT_ROUTES = [
    ...layoutsRoutes,
    ...homeRoutes,
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
