import {Routes} from '@angular/router';
import {AttachmentComponent} from './attachment.component';
import {UserRouteAccessService} from '../../shared';

export const attachmentRoutes: Routes = [
    {
        path: 'attachment',
        component: AttachmentComponent,
        data: {
            authorities: ['ROLE_CONTENT'],
            pageTitle: 'CubeAI ★ 智立方'
        },
        canActivate: [UserRouteAccessService],
    },
];
