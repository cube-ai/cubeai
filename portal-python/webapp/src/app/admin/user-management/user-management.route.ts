import { Routes } from '@angular/router';
import { UserMgmtComponent } from './user-management.component';
import {UserRouteAccessService} from '../../shared';
import {UserDetailComponent} from "./user-detail.component";


export const userMgmtRoute: Routes = [
    {
        path: 'user-management',
        component: UserMgmtComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方',
            authorities: ['ROLE_ADMIN'],
        },
        canActivate: [UserRouteAccessService],
    },
    {
        path: 'user-detail/:mode/:login',
        component: UserDetailComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'CubeAI ★ 智立方'
        },
        canActivate: [UserRouteAccessService],
    },
];
