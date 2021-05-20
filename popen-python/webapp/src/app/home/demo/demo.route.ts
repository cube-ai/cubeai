import {Routes} from '@angular/router';
import {DemoComponent} from './demo.component';

export const demoRoutes: Routes = [
    {
        path: 'demo',
        component: DemoComponent,
        data: {
            authorities: [],
            pageTitle: 'CubeAI ★ 智立方',
        },
    },
];
