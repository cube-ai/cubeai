import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { PrimengModule } from '../primeng.module';
import { AppSharedModule } from '../shared';

import {messageRoutes} from './message.route';
import {MessageComponent} from './message.component';
import {InboxComponent} from './inbox/inbox.component';
import {SentComponent} from './sent/sent.component';
import {DeletedComponent} from './deleted/deleted.component';

@NgModule({
    imports: [
        FormsModule,
        ReactiveFormsModule,
        PrimengModule,
        AppSharedModule,
        RouterModule.forChild(messageRoutes),
    ],
    declarations: [
        MessageComponent,
        InboxComponent,
        SentComponent,
        DeletedComponent,
    ],
    entryComponents: [
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AppMessageModule {}
