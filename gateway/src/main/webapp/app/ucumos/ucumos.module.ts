import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialModule} from '../material.module';
import { GatewaySharedModule } from '../shared';
import { FileUploadModule } from 'ng2-file-upload';
import { PopoverModule, BsDropdownModule } from 'ngx-bootstrap';
import { UEditorModule } from 'ngx-ueditor';

import {
    ucumosRoutes,
    UcumosComponent,
    UcumosNavbarComponent,
    UcumosHomeComponent,
    MarketComponent,
    PersonalComponent,
    OchestratorComponent,
    OnboardingComponent,
    DeployComponent,
    ApproveComponent,
    FavoriteComponent,
    SharedComponent,
    TaskComponent,
    TaskOnboardingComponent,
    SolutionComponent,
    PictureSelectComponent,
    ApproveHistoryComponent,
    ShareSolutionComponent,
    PackagingComponent,
    SolutionService,
    DescriptionService,
    OnboardingService,
    TaskStepService,
    TaskService,
    PublishRequestService,
    DownloadService,
    ArtifactService,
    DocumentService,
    SolutionFavoriteService,
    SolutionSharedService,
    SolutionRatingService,
    CommentService,
    DeployService,
    AbilityService,
    AddSolutionComponent,
    OchestratorService,
    CompositeSolutionService,
    OchestratorEditeLinkComponent,
    OchestratorEditeNodeComponent
} from './';

@NgModule({
    imports: [
        MaterialModule,
        GatewaySharedModule,
        FileUploadModule,
        RouterModule.forChild(ucumosRoutes),
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
        UcumosComponent,
        UcumosNavbarComponent,
        UcumosHomeComponent,
        MarketComponent,
        PersonalComponent,
        OchestratorComponent,
        OnboardingComponent,
        DeployComponent,
        ApproveComponent,
        FavoriteComponent,
        SharedComponent,
        TaskComponent,
        TaskOnboardingComponent,
        SolutionComponent,
        PictureSelectComponent,
        ApproveHistoryComponent,
        ShareSolutionComponent,
        PackagingComponent,
        AddSolutionComponent,
        OchestratorEditeLinkComponent,
        OchestratorEditeNodeComponent
    ],
    entryComponents: [
        PictureSelectComponent,
        ApproveHistoryComponent,
        ShareSolutionComponent,
        AddSolutionComponent,
        OchestratorEditeLinkComponent,
        OchestratorEditeNodeComponent
    ],
    providers: [
        SolutionService,
        DescriptionService,
        OnboardingService,
        TaskStepService,
        TaskService,
        PublishRequestService,
        DownloadService,
        ArtifactService,
        DocumentService,
        SolutionFavoriteService,
        SolutionSharedService,
        SolutionRatingService,
        CommentService,
        DeployService,
        AbilityService,
        OchestratorService,
        CompositeSolutionService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class GatewayUcumosModule {}
