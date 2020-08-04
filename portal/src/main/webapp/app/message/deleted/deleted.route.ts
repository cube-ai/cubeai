import {Routes} from '@angular/router';
import {DeletedComponent} from './deleted.component';
import {UserRouteAccessService} from '../../shared';

export const deletedRoutes: Routes = [
    {
        path: 'msg-deleted',
        component: DeletedComponent,
        data: {
            authorities: [],
            pageTitle: '已删除',
        },
        canActivate: [UserRouteAccessService],
    },
];
