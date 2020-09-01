import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MaterialModule} from '../material.module';
import { AppSharedModule } from '../shared';
import { FileUploadModule } from 'ng2-file-upload';
import { PopoverModule, BsDropdownModule } from 'ngx-bootstrap';

import {
    UmmClient,
    UmuClient,
    UmdClient,
    HomeComponent,
    PageHeaderComponent,
    MarketComponent,
    PersonalComponent,
    DemoComponent,
    AbilityComponent,
    StargazerComponent,
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
        UmmClient,
        UmuClient,
        UmdClient,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AppHomeModule {}
