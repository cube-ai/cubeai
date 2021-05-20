import {Routes} from '@angular/router';
import {UserRouteAccessService} from '../../shared';
import {OnboardingComponent} from './onboarding.component';
import {TaskOnboardingComponent} from "./task-onboarding.component";

export const onboardingRoutes: Routes = [
    {
        path: 'onboarding',
        component: OnboardingComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'CubeAI ★ 智立方'
        },
        canActivate: [UserRouteAccessService],
    },
    {
        path: 'task-onboarding/:taskUuid/:taskName',
        component: TaskOnboardingComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'CubeAI ★ 智立方'
        },
        canActivate: [UserRouteAccessService],
    }
];
