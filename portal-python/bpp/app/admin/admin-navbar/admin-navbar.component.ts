import { Component } from '@angular/core';
import {Principal} from '../../shared';

@Component({
    selector: 'my-admin-navbar',
    templateUrl: './admin-navbar.component.html',
    styleUrls: [
        'admin-navbar.css'
    ]
})
export class AdminNavbarComponent {
    isMobile = window.screen.width < 960;

    constructor(
        private principal: Principal,
    ) {}

    hasAuthority(authority: string) {
        return this.principal.hasAuthority(authority);
    }
}
