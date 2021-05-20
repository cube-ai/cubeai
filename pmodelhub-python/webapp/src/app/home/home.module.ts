import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { PrimengModule } from '../primeng.module';
import { FileUploadModule } from 'ng2-file-upload';
import { UEditorModule } from 'ngx-ueditor';
import { ImageCropperModule } from 'ngx-image-cropper';
import { AppSharedModule } from '../shared';

import {UmmClient} from './service/umm_client.service';
import {UmuClient} from './service/umu_client.service';
import {UmdClient} from './service/umd_client.service';
import {PageHeaderComponent} from './page-header.component';
import {HomeComponent} from './home.component';
import {MarketComponent} from './market/market.component';
import {PersonalComponent} from './personal/personal.component';
import {StargazerComponent} from './stargazer/stargazer.component';
import {SolutionComponent} from './solution/solution.component';
import {OnboardingComponent} from './onboarding/onboarding.component';
import {TaskOnboardingComponent} from './onboarding/task-onboarding.component';
import {PackagingComponent} from './packaging/packaging.component';
import {DeployComponent} from './deploy/deploy.component';

@NgModule({
    imports: [
        FormsModule,
        ReactiveFormsModule,
        PrimengModule,
        DragDropModule,
        AppSharedModule,
        FileUploadModule,
        UEditorModule.forRoot({
            js: [
                `assets/js/ueditor/ueditor.all.min.js`,
                `assets/js/ueditor/ueditor.config.js`,
            ],
            options: {
                UEDITOR_HOME_URL: 'assets/js/ueditor/'
            }
        }),
        ImageCropperModule,
    ],
    declarations: [
        HomeComponent,
        PageHeaderComponent,
        MarketComponent,
        PersonalComponent,
        StargazerComponent,
        SolutionComponent,
        OnboardingComponent,
        TaskOnboardingComponent,
        PackagingComponent,
        DeployComponent,
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
