import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MaterialModule} from '../material.module';
import { AppSharedModule } from '../shared';
import { FileUploadModule } from 'ng2-file-upload';
import { PopoverModule, BsDropdownModule } from 'ngx-bootstrap';
import { UEditorModule } from 'ngx-ueditor';

import {
    HomeComponent,
    PageHeaderComponent,
    SolutionComponent,
    PictureSelectComponent,
    MarketComponent,
    PersonalComponent,
    OnboardingComponent,
    TaskOnboardingComponent,
    PackagingComponent,
    DeployComponent,
    StargazerComponent,
    SolutionService,
    DescriptionService,
    OnboardingService,
    TaskStepService,
    TaskService,
    DownloadService,
    ArtifactService,
    DocumentService,
    StarService,
    CommentService,
    DeployService,
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
        UEditorModule.forRoot({
            js: [
                `./static/js/ueditor/ueditor.all.min.js`,
                `./static/js/ueditor/ueditor.config.js`,
            ],
            options: {
                UEDITOR_HOME_URL: './static/js/ueditor/'
            }
        }),
        /* jhipster-needle-add-admin-module - JHipster will add admin modules here */
    ],
    declarations: [
        HomeComponent,
        PageHeaderComponent,
        SolutionComponent,
        PictureSelectComponent,
        MarketComponent,
        PersonalComponent,
        OnboardingComponent,
        TaskOnboardingComponent,
        PackagingComponent,
        DeployComponent,
        StargazerComponent,
    ],
    entryComponents: [
        PictureSelectComponent,
    ],
    providers: [
        SolutionService,
        DescriptionService,
        OnboardingService,
        TaskStepService,
        TaskService,
        DownloadService,
        ArtifactService,
        DocumentService,
        StarService,
        CommentService,
        DeployService,
        AbilityService,
        CreditService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AppHomeModule {}
