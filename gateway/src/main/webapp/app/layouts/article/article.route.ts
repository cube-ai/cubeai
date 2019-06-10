import {Route} from '@angular/router';
import {ArticleComponent} from './article.component';

export const homeArticleRoute: Route = {
    path: 'article/:subject',
    component: ArticleComponent,
    data: {
        pageTitle: 'CubeAI'
    },
};
