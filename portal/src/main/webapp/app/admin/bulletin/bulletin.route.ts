import {Routes} from '@angular/router';
import {BulletinComponent} from './bulletin.component';
import {UserRouteAccessService} from '../../shared';

export const bulletinRoutes: Routes = [
    {
        path: 'bulletin',
        component: BulletinComponent,
        data: {
            authorities: ['ROLE_CONTENT'],
            pageTitle: '文稿管理'
        },
        canActivate: [UserRouteAccessService],
    },
];
