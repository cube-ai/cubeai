import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';

@Component({
    templateUrl: './message-home.component.html'
})
export class MessageHomeComponent implements OnInit {

    constructor(
        private router: Router,
    ) {

    }

    ngOnInit() {
        this.router.navigate(['/message/msg-inbox']);
    }
}
