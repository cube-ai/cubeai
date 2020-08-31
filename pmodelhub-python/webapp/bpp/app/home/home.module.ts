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
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AppHomeModule {}
