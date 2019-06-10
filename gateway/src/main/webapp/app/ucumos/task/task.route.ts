import {Routes} from '@angular/router';
import {TaskComponent} from './task.component';
import {UserRouteAccessService} from '../../shared';
import {TaskOnboardingComponent} from './task-onboarding.component';

export const taskRoutes: Routes = [
    {
        path: 'task',
        component: TaskComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: '模型导入'
        },
        canActivate: [UserRouteAccessService],
    },
    {
        path: 'task-onboarding/:taskUuid/:taskName',
        component: TaskOnboardingComponent,
        data: {
            pageTitle: '模型导入任务详情'
        }
    }
];
