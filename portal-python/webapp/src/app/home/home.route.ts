import {Routes} from '@angular/router';
import {HomeComponent} from './home.component';
import {AppNavigateComponent} from './appnav/app-navigate.component';
import {ArticleComponent} from "./article/article.component";

export const homeRoutes: Routes = [
    {
        path: '',
        component: HomeComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方'
        },
    },
    {
        path: 'app/:navTitle',
        component: AppNavigateComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方'
        },
    },
    {
        path: 'article/:subject',
        component: ArticleComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方'
        },
    }
];
