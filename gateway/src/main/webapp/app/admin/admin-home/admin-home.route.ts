import { Routes } from '@angular/router';

import { AdminHomeComponent } from './admin-home.component';

export const adminHomeRoutes: Routes = [
    {
        path: '',
        component: AdminHomeComponent,
        data: {
            pageTitle: '系统管理'
        },
    },
    {
        path: 'home',
        component: AdminHomeComponent,
        data: {
            pageTitle: '系统管理'
        },
    },
];
