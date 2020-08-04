import {Routes} from '@angular/router';
import { PoemWriterComponent } from './poem-writer.component';

export const poemWriterRoutes: Routes = [{
    path: 'poemwriter',
    component: PoemWriterComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
