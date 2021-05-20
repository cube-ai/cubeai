import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { PrimengModule } from '../primeng.module';
import { FileUploadModule } from 'ng2-file-upload';
import { UEditorModule } from 'ngx-ueditor';
import { ImageCropperModule } from 'ngx-image-cropper';
import { AppSharedModule } from '../shared';

import {adminRoutes} from './admin.route';
import {AdminComponent} from './admin.component';
import {RoleMgmtComponent} from './role-management/role-management.component';
import {UserMgmtComponent} from './user-management/user-management.component';
import {UserDetailComponent} from './user-management/user-detail.component';
import {ApplicationComponent} from './application/application.component';
import {AppdetailComponent} from './application/appdetail.component';
import {ArticlesComponent} from './articles/articles.component';
import {ArticleComponent} from './articles/article.component';
import {AttachmentComponent} from './attachment/attachment.component';

@NgModule({
    imports: [
        FormsModule,
        ReactiveFormsModule,
        PrimengModule,
        FileUploadModule,
        RouterModule.forChild(adminRoutes),
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
        AppSharedModule,
    ],
    declarations: [
        AdminComponent,
        RoleMgmtComponent,
        UserMgmtComponent,
        UserDetailComponent,
        ApplicationComponent,
        AppdetailComponent,
        ArticlesComponent,
        ArticleComponent,
        AttachmentComponent,
    ],
    entryComponents: [
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppAdminModule {}
