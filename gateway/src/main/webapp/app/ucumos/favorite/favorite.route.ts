import {Routes} from '@angular/router';
import {FavoriteComponent} from './favorite.component';
import {UserRouteAccessService} from '../../shared';

export const favoriteRoutes: Routes = [
    {
        path: 'favorite',
        component: FavoriteComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: '我的收藏'
        },
        canActivate: [UserRouteAccessService],
    },
];
