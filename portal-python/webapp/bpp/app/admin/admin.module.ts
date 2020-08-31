import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialModule} from '../material.module';
import { AppSharedModule } from '../shared';
import { FileUploadModule } from 'ng2-file-upload';
import { UEditorModule } from 'ngx-ueditor';

import {
    adminRoutes,
    AdminComponent,
    AdminNavbarComponent,
    UserMgmtComponent,
    UserDetailsComponent,
    UserPasswordComponent,
    RoleMgmtComponent,
    UserResolve,
    BulletinComponent,
    ArticleComponent,
    AttachmentComponent,
    AppdetailComponent,
    ApplicationComponent,
} from './';

@NgModule({
    imports: [
        MaterialModule,
        AppSharedModule,
        FileUploadModule,
        RouterModule.forChild(adminRoutes),
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
        AdminComponent,
        AdminNavbarComponent,
        UserMgmtComponent,
        UserDetailsComponent,
        UserPasswordComponent,
        RoleMgmtComponent,
        BulletinComponent,
        ArticleComponent,
        AttachmentComponent,
        ApplicationComponent,
        AppdetailComponent,
    ],
    entryComponents: [
        UserDetailsComponent,
        UserPasswordComponent,
    ],
    providers: [
        UserResolve,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AppAdminModule {}
