import {Routes} from '@angular/router';
import { SceneClassifyComponent } from './scene-classify.component';

export const sceneClassifyRoutes: Routes = [{
    path: 'scene-classify',
    component: SceneClassifyComponent,
    data: {
        pageTitle: 'CubeAI开放能力演示'
    },
},
];
