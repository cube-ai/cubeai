import {Routes} from '@angular/router';
import {AbilityComponent} from './ability.component';

export const abilityRoutes: Routes = [
    {
        path: 'ability/:abilityUuid/:openMode',
        component: AbilityComponent,
        data: {
            pageTitle: 'AI模型部署实例'
        }
    },
];
