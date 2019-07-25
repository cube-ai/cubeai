import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Principal} from '../../account';

@Component({
    templateUrl: './admin-home.component.html'
})
export class AdminHomeComponent implements OnInit {

    constructor(
        private principal: Principal,
        private router: Router,
    ) {
    }

    ngOnInit() {
        if (this.principal.hasAuthority('ROLE_ADMIN')) {
            this.router.navigate(['/admin/user-management']);
        } else if (this.principal.hasAuthority('ROLE_CONTENT')) {
            this.router.navigate(['/admin/bulletin']);
        }
    }
}
