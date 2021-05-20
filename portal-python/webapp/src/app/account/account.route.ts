import { Routes } from '@angular/router';
import {loginRoutes} from './login/login.route';
import {registerRoutes} from './register/register.route';
import {activateRoutes} from './activate/activate.route';
import {passwordresetRoutes} from './password-reset/password-reset.route';
import {settingsRoutes} from './settings/settings.route';

export const accountRoutes: Routes = [
    ...loginRoutes,
    ...registerRoutes,
    ...activateRoutes,
    ...passwordresetRoutes,
    ...settingsRoutes,
];
