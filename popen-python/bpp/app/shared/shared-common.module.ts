import { NgModule, LOCALE_ID } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { registerLocaleData } from '@angular/common';
import locale from '@angular/common/locales/en';

import { WindowRef } from './service/window.service';
import {
    AppSharedLibsModule,
} from './';

@NgModule({
    imports: [
        AppSharedLibsModule
    ],
    declarations: [
    ],
    providers: [
        WindowRef,
        Title,
        {
            provide: LOCALE_ID,
            useValue: 'en'
        },
    ],
    exports: [
        AppSharedLibsModule,
    ]
})
export class AppSharedCommonModule {
    constructor() {
        registerLocaleData(locale);
    }
}
