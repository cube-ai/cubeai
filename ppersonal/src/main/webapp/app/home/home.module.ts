import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MaterialModule} from '../material.module';
import { AppSharedModule } from '../shared';
import { FileUploadModule } from 'ng2-file-upload';
import { PopoverModule, BsDropdownModule } from 'ngx-bootstrap';

import {
    HomeComponent,
    PageHeaderComponent,
    StarComponent,
    TaskComponent,
    TaskAdminComponent,
    CreditComponent,
    CreditAdminComponent,
    CreditDetailComponent,
    SolutionService,
    TaskService,
    StarService,
    AbilityService,
    CreditService,
} from './';

@NgModule({
    imports: [
        MaterialModule,
        AppSharedModule,
        FileUploadModule,
        BsDropdownModule.forRoot(),
        PopoverModule.forRoot(),
    ],
    declarations: [
        HomeComponent,
        PageHeaderComponent,
        StarComponent,
        TaskComponent,
        TaskAdminComponent,
        CreditComponent,
        CreditAdminComponent,
        CreditDetailComponent,
    ],
    entryComponents: [
    ],
    providers: [
        SolutionService,
        TaskService,
        StarService,
        AbilityService,
        CreditService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AppHomeModule {}
