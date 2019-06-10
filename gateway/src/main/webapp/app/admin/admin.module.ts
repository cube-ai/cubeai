import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MaterialModule} from '../material.module';
import { GatewaySharedModule } from '../shared';
import { UEditorModule } from 'ngx-ueditor';

import {
    adminRoutes,
    AdminComponent,
    AdminNavbarComponent,
    AdminHomeComponent,
    UserMgmtComponent,
    UserDetailsComponent,
    UserPasswordComponent,
    JhiMetricsMonitoringModalComponent,
    JhiMetricsMonitoringComponent,
    JhiHealthModalComponent,
    JhiHealthCheckComponent,
    JhiConfigurationComponent,
    JhiDocsComponent,
    JhiConfigurationService,
    JhiHealthService,
    JhiMetricsService,
    GatewayRoutesService,
    JhiGatewayComponent,
    UserResolve,
    BulletinComponent,
    ArticleComponent,
    ArticleService,
} from './';

@NgModule({
    imports: [
        MaterialModule,
        GatewaySharedModule,
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
        /* jhipster-needle-add-admin-module - JHipster will add admin modules here */
    ],
    declarations: [
        AdminComponent,
        AdminNavbarComponent,
        AdminHomeComponent,
        UserMgmtComponent,
        UserDetailsComponent,
        UserPasswordComponent,
        JhiConfigurationComponent,
        JhiHealthCheckComponent,
        JhiDocsComponent,
        JhiHealthModalComponent,
        JhiGatewayComponent,
        JhiMetricsMonitoringComponent,
        JhiMetricsMonitoringModalComponent,
        BulletinComponent,
        ArticleComponent,
    ],
    entryComponents: [
        UserDetailsComponent,
        UserPasswordComponent,
        JhiHealthModalComponent,
        JhiMetricsMonitoringModalComponent,
    ],
    providers: [
        JhiConfigurationService,
        JhiHealthService,
        JhiMetricsService,
        GatewayRoutesService,
        UserResolve,
        ArticleService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GatewayAdminModule {}
