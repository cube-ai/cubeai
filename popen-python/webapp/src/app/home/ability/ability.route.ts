import {Routes} from '@angular/router';
import {AbilityComponent} from './ability.component';

export const abilityRoutes: Routes = [
    {
        path: 'ability/:solutionUuid',
        component: AbilityComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方'
        }
    },
];
