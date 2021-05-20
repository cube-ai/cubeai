import {Routes} from '@angular/router';
import {SentComponent} from './sent.component';
import {UserRouteAccessService} from '../../shared';

export const sentRoutes: Routes = [
    {
        path: 'msg-sent',
        component: SentComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'CubeAI ★ 智立方',
        },
        canActivate: [UserRouteAccessService],
    },
];
