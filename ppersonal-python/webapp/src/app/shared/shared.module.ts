import {NgModule, CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {DatePipe} from '@angular/common';
import { PrimengModule} from '../primeng.module';

import { MdEditorDirective } from './directive/md-editor.directive';
import { MdViewDirective } from './directive/md-view.directive';
import { HtmlPipe } from './pipe/html.pipe';
import { Principal } from './service/principal.service';
import { GlobalService } from './service/global.service';
import { LoginService } from './service/login.service';
import { UaaClient } from './service/uaa_client.service';

@NgModule({
    imports: [
        PrimengModule,
    ],
    declarations: [
        MdEditorDirective,
        MdViewDirective,
        HtmlPipe,
    ],
    entryComponents: [
    ],
    providers: [
        DatePipe,
        Principal,
        GlobalService,
        LoginService,
        UaaClient,
    ],
    exports: [
        MdEditorDirective,
        MdViewDirective,
        DatePipe,
        HtmlPipe,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AppSharedModule {
}
