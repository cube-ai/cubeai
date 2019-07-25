import { Component } from '@angular/core';
import {Principal} from '../../account';

@Component({
    selector: 'jhi-admin-navbar',
    templateUrl: './admin-navbar.component.html',
    styleUrls: [
        'admin-navbar.css'
    ]
})
export class AdminNavbarComponent {
    constructor(
        private principal: Principal,
    ) {}

    hasAuthority(authority: string) {
        return this.principal.hasAuthority(authority);
    }
}
