import {Routes} from '@angular/router';
import {SolutionComponent} from './solution.component';

export const solutionRoutes: Routes = [
    {
        path: 'solution/:solutionUuid',
        component: SolutionComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方'
        }
    },
];
