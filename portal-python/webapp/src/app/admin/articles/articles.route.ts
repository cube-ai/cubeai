import {Routes} from '@angular/router';
import {ArticlesComponent} from './articles.component';
import {ArticleComponent} from "./article.component";
import {UserRouteAccessService} from '../../shared';

export const articlesRoutes: Routes = [
    {
        path: 'articles',
        component: ArticlesComponent,
        data: {
            authorities: ['ROLE_CONTENT'],
            pageTitle: 'CubeAI ★ 智立方'
        },
        canActivate: [UserRouteAccessService],
    },
    {
        path: 'article/:mode/:id',
        component: ArticleComponent,
        data: {
            authorities: ['ROLE_CONTENT'],
            pageTitle: 'CubeAI ★ 智立方'
        },
        canActivate: [UserRouteAccessService],
    },
];
