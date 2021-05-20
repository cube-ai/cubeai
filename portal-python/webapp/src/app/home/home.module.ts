import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { PrimengModule } from '../primeng.module';
import { AppSharedModule } from '../shared';

import {HomeComponent} from './home.component';
import {AppNavigateComponent} from './appnav/app-navigate.component';
import {ArticleComponent} from './article/article.component';

@NgModule({
    imports: [
        PrimengModule,
        AppSharedModule,
    ],
    declarations: [
        HomeComponent,
        AppNavigateComponent,
        ArticleComponent,
    ],
    entryComponents: [
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AppHomeModule {}
