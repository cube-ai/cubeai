import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { homeRoutes } from './home/home.route';

const ROOT_ROUTES: Routes = [
    ...homeRoutes,
];

@NgModule({
    imports: [
        RouterModule.forRoot(ROOT_ROUTES, { useHash: true })
    ],
    exports: [
        RouterModule,
    ]
})
export class AppRoutingModule {
}
