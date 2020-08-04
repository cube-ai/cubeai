import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';

@Component({
    templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {
    constructor(
        private router: Router,
    ) {}

    ngOnInit() {
    }

    navigateTo(url: string) {
        this.router.navigate([url]);
    }

}
