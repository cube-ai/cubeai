import {Routes} from '@angular/router';
import {AbilityComponent} from './ability.component';

export const abilityRoutes: Routes = [
    {
        path: 'ability/:abilityUuid',
        component: AbilityComponent,
        data: {
            pageTitle: 'AI开放能力'
        }
    },
];
