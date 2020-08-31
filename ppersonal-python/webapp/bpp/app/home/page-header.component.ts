import {Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({
    selector: 'my-pageheader',
    templateUrl: './page-header.component.html',
})
export class PageHeaderComponent  {
    isMobile = window.screen.width < 960;

    constructor(
        private router: Router,
    ) {}

    gotoHome() {
        this.router.navigate(['/']);
    }
}
