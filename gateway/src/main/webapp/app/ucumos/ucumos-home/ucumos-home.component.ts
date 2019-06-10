import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';

@Component({
    templateUrl: './ucumos-home.component.html'
})
export class UcumosHomeComponent implements OnInit {
    currentAccount: any;

    constructor(
        private router: Router,
    ) {

    }

    ngOnInit() {
        this.router.navigate(['/ucumos/market']);
    }
}
