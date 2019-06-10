import {NgModule, CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {DatePipe} from '@angular/common';
import { MaterialModule} from '../material.module';
import {MatPaginatorIntl} from '@angular/material';

import {
    GatewaySharedLibsModule,
    GatewaySharedCommonModule,
    StateStorageService,
    HasAnyAuthorityDirective,
    ArrayFilter,
    HtmlPipe,
    PageItemCountComponent,
    PasswordStrengthBarComponent,
    SnackBarService,
    ConfirmService,
    ConfirmDialogComponent,
    MatPaginatorCn,
    GlobalService,
} from './';

@NgModule({
    imports: [
        MaterialModule,
        GatewaySharedLibsModule,
        GatewaySharedCommonModule,
    ],
    declarations: [
        HasAnyAuthorityDirective,
        ArrayFilter,
        HtmlPipe,
        PageItemCountComponent,
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
        {provide: MatPaginatorIntl, useClass: MatPaginatorCn},
    ],
    exports: [
        GatewaySharedCommonModule,
        HasAnyAuthorityDirective,
        DatePipe,
        ArrayFilter,
        HtmlPipe,
        PageItemCountComponent,
        PasswordStrengthBarComponent,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class GatewaySharedModule {
}
