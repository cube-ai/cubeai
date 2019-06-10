import {Routes} from '@angular/router';
import {OnboardingComponent} from './onboarding.component';
import {UserRouteAccessService} from '../../shared';

export const onboardingRoutes: Routes = [
    {
        path: 'onboarding',
        component: OnboardingComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: '模型导入'
        },
        canActivate: [UserRouteAccessService],
    },
];
