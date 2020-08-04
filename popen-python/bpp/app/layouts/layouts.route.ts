import { Routes } from '@angular/router';

import {
    articleRoutes,
    errorRoutes,
} from './';

export const layoutsRoutes: Routes = [
    ...articleRoutes,
    ...errorRoutes,
];
