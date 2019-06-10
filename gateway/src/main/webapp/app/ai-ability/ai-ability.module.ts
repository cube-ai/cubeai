import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialModule} from '../material.module';
import { GatewaySharedModule } from '../shared';
import { FileUploadModule } from 'ng2-file-upload';
import { PopoverModule, BsDropdownModule } from 'ngx-bootstrap';

import {
    aiAbilityRoutes,
    AiAbilityComponent,
    AiAbilityNavbarComponent,
    AiAbilityHomeComponent,
    OpenAbilityComponent,
    MyAbilityComponent,
    AllAbilityComponent,
    AbilityComponent,
    AbilityService,
} from './';

@NgModule({
    imports: [
        MaterialModule,
        GatewaySharedModule,
        FileUploadModule,
        RouterModule.forChild(aiAbilityRoutes),
        BsDropdownModule.forRoot(),
        PopoverModule.forRoot(),
        /* jhipster-needle-add-admin-module - JHipster will add admin modules here */
    ],
    declarations: [
        AiAbilityComponent,
        AiAbilityNavbarComponent,
        AiAbilityHomeComponent,
        OpenAbilityComponent,
        MyAbilityComponent,
        AllAbilityComponent,
        AbilityComponent,
    ],
    entryComponents: [
    ],
    providers: [
        AbilityService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class GatewayAiAbilityModule {}
