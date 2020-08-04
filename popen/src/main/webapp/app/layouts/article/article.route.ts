import {Routes} from '@angular/router';
import {ArticleComponent} from './article.component';

export const articleRoutes: Routes = [
    {
        path: 'article/:subject',
        component: ArticleComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方'
        },
    }
];
