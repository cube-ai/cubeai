import { Component } from '@angular/core';
import {Router} from '@angular/router';

@Component({
    selector: 'jhi-footer',
    templateUrl: './footer.component.html',
    styleUrls: [
        'footer.css'
    ]
})
export class FooterComponent {

    constructor(
        private router: Router,
    ) {
    }

    navigateToTechniqueSupport() {
        this.router.navigate(['/article/cubeai-technique-support']);
    }

    navigateToAboutMe() {
        this.router.navigate(['/article/cubeai-aboutme']);
    }
}
