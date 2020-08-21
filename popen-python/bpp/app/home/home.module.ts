import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MaterialModule} from '../material.module';
import { AppSharedModule } from '../shared';
import { FileUploadModule } from 'ng2-file-upload';
import { PopoverModule, BsDropdownModule } from 'ngx-bootstrap';

import {
    HomeComponent,
    PageHeaderComponent,
    MarketComponent,
    PersonalComponent,
    DemoComponent,
    AbilityComponent,
    StargazerComponent,
    SolutionService,
    TaskStepService,
    TaskService,
    StarService,
    AbilityService,
    DocumentService,
    DownloadService,
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
        MarketComponent,
        PersonalComponent,
        DemoComponent,
        AbilityComponent,
        StargazerComponent,
    ],
    entryComponents: [
    ],
    providers: [
        SolutionService,
        TaskStepService,
        TaskService,
        StarService,
        AbilityService,
        DocumentService,
        DownloadService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AppHomeModule {}
