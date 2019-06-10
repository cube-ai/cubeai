import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialModule} from '../material.module';
import { GatewaySharedModule } from '../shared';

import {
    AuthServerProvider,
    AccountService,
    UserService,
    LoginService,
    Principal,
    RegisterService,
    ActivateService,
    PasswordService,
    PasswordResetInitService,
    PasswordResetFinishService,
    RegisterComponent,
    LoginComponent,
    PasswordComponent,
    PasswordResetComponent,
    SettingsComponent,
    VerifyCodeService,
} from './';

@NgModule({
    imports: [
        MaterialModule,
        GatewaySharedModule,
    ],
    declarations: [
        RegisterComponent,
        LoginComponent,
        PasswordComponent,
        PasswordResetComponent,
        SettingsComponent
    ],
    entryComponents: [
        RegisterComponent,
        LoginComponent,
        PasswordComponent,
        PasswordResetComponent,
        SettingsComponent
    ],
    providers: [
        AuthServerProvider,
        AccountService,
        UserService,
        LoginService,
        Principal,
        RegisterService,
        ActivateService,
        PasswordService,
        PasswordResetInitService,
        PasswordResetFinishService,
        VerifyCodeService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GatewayAccountModule {}
