import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { PrimengModule} from './primeng.module';
import { MessageService } from 'primeng/api';
import {ConfirmationService} from 'primeng/api';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent, FooterComponent } from './layouts';
import { AppSharedModule, UserRouteAccessService } from './shared';
import { AppHomeModule } from './home/home.module';
import { AppAccountModule } from './account/account.module';
import { AppAdminModule } from './admin/admin.module';
import { AppMessageModule } from './message/message.module';

@NgModule({
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule,
        PrimengModule,

        AppRoutingModule,
        AppSharedModule,
        AppHomeModule,
        AppAccountModule,
        AppAdminModule,
        AppMessageModule,
    ],
    declarations: [
        AppComponent,
        HeaderComponent,
        FooterComponent,
    ],
    providers: [
        MessageService,
        ConfirmationService,
        UserRouteAccessService,
    ],
    bootstrap: [
        AppComponent,
    ]
})
export class AppModule {
}
