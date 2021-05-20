import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PrimengModule} from '../primeng.module';
import { FileUploadModule } from 'ng2-file-upload';
import { ImageCropperModule } from 'ngx-image-cropper';
import { AppSharedModule } from '../shared';

import {LoginComponent} from './login/login.component';
import {RegisterComponent} from './register/register.component';
import {ActivateComponent} from './activate/activate.component';
import {PasswordResetComponent} from './password-reset/password-reset.component';
import {SettingsComponent} from './settings/settings.component';

@NgModule({
    imports: [
        FormsModule,
        ReactiveFormsModule,
        PrimengModule,
        FileUploadModule,
        ImageCropperModule,
        AppSharedModule,
    ],
    declarations: [
        LoginComponent,
        RegisterComponent,
        ActivateComponent,
        PasswordResetComponent,
        SettingsComponent,
    ],
    entryComponents: [
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppAccountModule {}
