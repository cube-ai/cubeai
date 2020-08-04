import {NgModule, CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {DatePipe} from '@angular/common';
import { MaterialModule} from '../material.module';
import {MatPaginatorIntl} from '@angular/material';

import {
    AppSharedLibsModule,
    AppSharedCommonModule,
    StateStorageService,
    ArrayFilter,
    HtmlPipe,
    PasswordStrengthBarComponent,
    SnackBarService,
    ConfirmService,
    ConfirmDialogComponent,
    MatPaginatorCn,
    GlobalService,
    AuthServerProvider,
    AccountService,
    UserService,
    LoginService,
    Principal,
    VerifyCodeService,
    ArticleService,
    ApplicationService,
    RandomPictureService,
    MessageService,
} from './';

@NgModule({
    imports: [
        MaterialModule,
        AppSharedLibsModule,
        AppSharedCommonModule,
    ],
    declarations: [
        ArrayFilter,
        HtmlPipe,
        PasswordStrengthBarComponent,
        ConfirmDialogComponent,
    ],
    entryComponents: [
        ConfirmDialogComponent,
    ],
    providers: [
        StateStorageService,
        DatePipe,
        SnackBarService,
        ConfirmService,
        GlobalService,
        AuthServerProvider,
        AccountService,
        UserService,
        LoginService,
        Principal,
        VerifyCodeService,
        ArticleService,
        ApplicationService,
        RandomPictureService,
        MessageService,
        {provide: MatPaginatorIntl, useClass: MatPaginatorCn},
    ],
    exports: [
        AppSharedCommonModule,
        DatePipe,
        ArrayFilter,
        HtmlPipe,
        PasswordStrengthBarComponent,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AppSharedModule {
}
