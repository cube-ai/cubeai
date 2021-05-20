import { Routes } from '@angular/router';
import { PackagingComponent } from './packaging.component';

export const packagingRoutes: Routes = [
    {
        path: 'packaging',
        component: PackagingComponent,
        data: {
            pageTitle: 'CubeAI ★ 智立方'
        },
    },
];
