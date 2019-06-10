import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';

@Component({
    templateUrl: './ai-ability-home.component.html'
})
export class AiAbilityHomeComponent implements OnInit {
    currentAccount: any;

    constructor(
        private router: Router,
    ) {

    }

    ngOnInit() {
        this.router.navigate(['/ai-ability/open-ability']);
    }
}
