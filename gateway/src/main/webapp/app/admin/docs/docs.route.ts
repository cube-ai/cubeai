import { Route } from '@angular/router';

import { JhiDocsComponent } from './docs.component';
import {UserRouteAccessService} from '../../shared';

export const docsRoute: Route = {
    path: 'docs',
    component: JhiDocsComponent,
    data: {
        pageTitle: 'API接口',
        authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
};
