import './vendor.ts';

import { NgModule, Injector } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { Ng2Webstorage } from 'ngx-webstorage';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AuthExpiredInterceptor } from './blocks/interceptor/auth-expired.interceptor';
import { MAT_DIALOG_DEFAULT_OPTIONS } from '@angular/material';
import { MaterialModule } from './material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AppMainRoutingModule} from './app-routing.module';
import { AppSharedModule, UserRouteAccessService } from './shared';
import { AppHomeModule } from './home';
import { AppAccountModule } from './account';
import { AppMessageModule } from './message';
import { AppAdminModule } from './admin';
import {
    MainComponent,
    HeaderComponent,
    FooterComponent,
    ErrorComponent,
    ArticleComponent,
} from './layouts';

@NgModule({
    imports: [
        FormsModule,
        ReactiveFormsModule,
        BrowserModule,
        BrowserAnimationsModule,
        MaterialModule,
        AppMainRoutingModule,
        Ng2Webstorage.forRoot({ prefix: 'my', separator: '-'}),
        AppSharedModule,
        AppHomeModule,
        AppAccountModule,
        AppMessageModule,
        AppAdminModule,
    ],
    declarations: [
        MainComponent,
        HeaderComponent,
        FooterComponent,
        ErrorComponent,
        ArticleComponent,
    ],
    providers: [
        UserRouteAccessService,
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthExpiredInterceptor,
            multi: true,
            deps: [
                Injector
            ]
        },
        {
            provide: MAT_DIALOG_DEFAULT_OPTIONS,
            useValue: {
                hasBackdrop: true,
                disableClose: true,
            }
        }
    ],
    bootstrap: [ MainComponent ]
})
export class AppMainModule {}
