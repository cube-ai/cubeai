import { Component } from '@angular/core';

@Component({
    selector: 'my-footer',
    templateUrl: './footer.component.html',
    styleUrls: [
        'footer.css'
    ]
})
export class FooterComponent {
    isMobile = window.screen.width < 960;

    navigateToTechniqueSupport() {
        window.location.href = '/#/article/cubeai-technique-support';
    }

    navigateToAboutMe() {
        window.location.href = '/#/article/cubeai-aboutme';
    }
}
