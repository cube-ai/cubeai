import { Routes } from '@angular/router';

import { MessageHomeComponent } from './message-home.component';

export const messageHomeRoutes: Routes = [
    {
        path: '',
        component: MessageHomeComponent,
        data: {
            pageTitle: '站内消息'
        },
    },
    {
        path: 'home',
        component: MessageHomeComponent,
        data: {
            pageTitle: '站内消息'
        },
    },
];
