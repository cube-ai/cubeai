import {Routes} from '@angular/router';
import { ImageStyleTransferComponent } from './image-style-transfer.component';

export const imageStyleTransferRoutes: Routes = [{
    path: 'imagestyletransfer',
    component: ImageStyleTransferComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
