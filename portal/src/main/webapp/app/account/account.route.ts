import { Routes } from '@angular/router';

import {
    activateRoutes,
    loginRoutes,
    registerRoutes,
    passwordresetRoutes,
    settingsRoutes,
} from './';

export const accountRoutes: Routes = [
    ...activateRoutes,
    ...loginRoutes,
    ...registerRoutes,
    ...passwordresetRoutes,
    ...settingsRoutes,
];
