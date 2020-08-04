import {Routes} from '@angular/router';
import {SettingsComponent} from './settings.component';
import {UserRouteAccessService} from '../../shared';

export const settingsRoutes: Routes = [
    {
        path: 'settings',
        component: SettingsComponent,
        data: {
            pageTitle: '我的帐号',
            authorities: ['ROLE_USER'],
        },
        canActivate: [UserRouteAccessService],
    },
];
