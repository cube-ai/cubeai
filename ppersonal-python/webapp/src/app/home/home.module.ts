import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { PrimengModule } from '../primeng.module';
import { AppSharedModule } from '../shared';

import {UmmClient} from './service/umm_client.service'
import {PageHeaderComponent} from './page-header.component';
import {HomeComponent} from './home.component';
import {StarComponent} from './star/star.component';
import {TaskComponent} from './task/task.component';
import {CreditComponent} from './credit/credit.component';
import {TaskAdminComponent} from './task-admin/task-admin.component';
import {CreditAdminComponent} from './credit-admin/credit-admin.component';
import {CreditDetailComponent} from './credit-admin/credit-detail.component';

@NgModule({
    imports: [
        PrimengModule,
        DragDropModule,
        AppSharedModule,
    ],
    declarations: [
        HomeComponent,
        PageHeaderComponent,
        StarComponent,
        TaskComponent,
        CreditComponent,
        TaskAdminComponent,
        CreditAdminComponent,
        CreditDetailComponent,
    ],
    entryComponents: [
    ],
    providers: [
        UmmClient,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AppHomeModule {}
