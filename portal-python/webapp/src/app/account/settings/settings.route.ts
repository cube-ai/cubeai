import {Routes} from '@angular/router';
import {SettingsComponent} from './settings.component';
import {UserRouteAccessService} from '../../shared';

export const settingsRoutes: Routes = [
    {
        path: 'settings',
        component: SettingsComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方',
            authorities: ['ROLE_USER'],
        },
        canActivate: [UserRouteAccessService],
    },
];
