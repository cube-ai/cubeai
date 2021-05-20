import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { homeRoutes } from './home/home.route';
import { accountRoutes } from './account/account.route';
import { adminRoutes } from './admin/admin.route';
import { messageRoutes } from './message/message.route';

const ROOT_ROUTES: Routes = [
    ...homeRoutes,
    ...accountRoutes,
    ...adminRoutes,
    ...messageRoutes,
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
