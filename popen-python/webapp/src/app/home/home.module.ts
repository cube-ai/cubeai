import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { PrimengModule } from '../primeng.module';
import { FileUploadModule } from 'ng2-file-upload';
import { AppSharedModule } from '../shared';

import {UmmClient} from './service/umm_client.service';
import {UmuClient} from './service/umu_client.service';
import {UmdClient} from './service/umd_client.service';
import {PageHeaderComponent} from './page-header.component';
import {HomeComponent} from './home.component';
import {MarketComponent} from './market/market.component';
import {PersonalComponent} from './personal/personal.component';
import {StargazerComponent} from './stargazer/stargazer.component';
import {DemoComponent} from './demo/demo.component';
import {AbilityComponent} from './ability/ability.component';

@NgModule({
    imports: [
        PrimengModule,
        DragDropModule,
        AppSharedModule,
        FileUploadModule,
    ],
    declarations: [
        HomeComponent,
        PageHeaderComponent,
        MarketComponent,
        PersonalComponent,
        StargazerComponent,
        DemoComponent,
        AbilityComponent,
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
