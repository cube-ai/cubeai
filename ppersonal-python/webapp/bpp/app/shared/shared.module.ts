import {NgModule, CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {DatePipe} from '@angular/common';
import { MaterialModule} from '../material.module';
import {MatPaginatorIntl} from '@angular/material';

import {
    AppSharedLibsModule,
    AppSharedCommonModule,
    HtmlPipe,
    PasswordStrengthBarComponent,
    SnackBarService,
    ConfirmService,
    ConfirmDialogComponent,
    MatPaginatorCn,
    GlobalService,
    Principal,
    LoginService,
    UaaClient,
} from './';

@NgModule({
    imports: [
        MaterialModule,
        AppSharedLibsModule,
        AppSharedCommonModule,
    ],
    declarations: [
        HtmlPipe,
        PasswordStrengthBarComponent,
        ConfirmDialogComponent,
    ],
    entryComponents: [
        ConfirmDialogComponent,
    ],
    providers: [
        DatePipe,
        SnackBarService,
        ConfirmService,
        GlobalService,
        Principal,
        LoginService,
        UaaClient,
        {provide: MatPaginatorIntl, useClass: MatPaginatorCn},
    ],
    exports: [
        AppSharedCommonModule,
        DatePipe,
        HtmlPipe,
        PasswordStrengthBarComponent,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AppSharedModule {
}
