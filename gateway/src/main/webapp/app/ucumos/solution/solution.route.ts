import {Routes} from '@angular/router';
import {SolutionComponent} from './solution.component';

export const solutionRoutes: Routes = [
    {
        path: 'solution/:solutionUuid/:openMode',
        component: SolutionComponent,
        data: {
            pageTitle: 'AI模型'
        }
    },
    {
        path: 'solution/:solutionUuid/:openMode/:publishRequestId',
        component: SolutionComponent,
        data: {
            pageTitle: 'AI模型'
        }
    },
];
