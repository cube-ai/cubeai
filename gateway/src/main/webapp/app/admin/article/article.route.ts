import {Routes} from '@angular/router';
import {ArticleComponent} from './article.component';
import {UserRouteAccessService} from '../../shared';

export const articleRoutes: Routes = [
    {
        path: 'article/:mode/:id',
        component: ArticleComponent,
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: '文稿'
        },
        canActivate: [UserRouteAccessService],
    },
];
