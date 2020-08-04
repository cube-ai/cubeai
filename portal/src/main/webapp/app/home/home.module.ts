import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MaterialModule} from '../material.module';
import { AppSharedModule } from '../shared';

import {
    HomeComponent,
    AppListComponent,
    AppNavigateComponent,
} from './';

@NgModule({
    imports: [
        MaterialModule,
        AppSharedModule,
    ],
    declarations: [
        HomeComponent,
        AppListComponent,
        AppNavigateComponent,
    ],
    entryComponents: [
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]

})
export class AppHomeModule {}
