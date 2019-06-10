import {Routes} from '@angular/router';
import {PersonalComponent} from './personal.component';
import {UserRouteAccessService} from '../../shared';

export const personalRoutes: Routes = [
    {
        path: 'personal',
        component: PersonalComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: '我的模型'
        },
        canActivate: [UserRouteAccessService],
    },
];
