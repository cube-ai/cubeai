import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialModule} from '../material.module';
import { AppSharedModule } from '../shared';

import {
    messageRoutes,
    MessageService,
    MessageComponent,
    MessageNavbarComponent,
    MessageHomeComponent,
    InboxComponent,
    DeletedComponent,
    SentComponent,
    MessageEditComponent,
} from './';

@NgModule({
    imports: [
        MaterialModule,
        AppSharedModule,
        RouterModule.forChild(messageRoutes),
        /* jhipster-needle-add-admin-module - JHipster will add admin modules here */
    ],
    declarations: [
        MessageComponent,
        MessageNavbarComponent,
        MessageHomeComponent,
        InboxComponent,
        DeletedComponent,
        SentComponent,
        MessageEditComponent,
    ],
    entryComponents: [
        MessageEditComponent,
    ],
    providers: [
        MessageService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AppMessageModule {}
