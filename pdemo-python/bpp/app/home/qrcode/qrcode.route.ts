import {Routes} from '@angular/router';
import { QrcodeComponent } from './qrcode.component';

export const qrcodeRoutes: Routes = [{
    path: 'qrcode',
    component: QrcodeComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
