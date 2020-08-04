import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MaterialModule} from '../material.module';
import { AppSharedModule } from '../shared';

import {
    RegisterService,
    ActivateService,
    PasswordService,
    PasswordResetInitService,
    PasswordResetFinishService,
    RegisterComponent,
    ActivateComponent,
    LoginComponent,
    PasswordResetComponent,
    SettingsComponent,
} from './';

@NgModule({
    imports: [
        MaterialModule,
        AppSharedModule,
    ],
    declarations: [
        RegisterComponent,
        ActivateComponent,
        LoginComponent,
        PasswordResetComponent,
        SettingsComponent,
    ],
    entryComponents: [
    ],
    providers: [
        RegisterService,
        ActivateService,
        PasswordService,
        PasswordResetInitService,
        PasswordResetFinishService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppAccountModule {}
