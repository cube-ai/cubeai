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
        this.router.navigate(['/admin/user-management']);
    }
}
