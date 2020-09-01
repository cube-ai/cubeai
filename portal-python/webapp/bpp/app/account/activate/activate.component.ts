import { Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UaaClient} from '../../shared';

@Component({
    templateUrl: './activate.component.html'
})
export class ActivateComponent implements OnInit {

    status = '';
    activateKey: string;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private uaaClient: UaaClient,
    ) {
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.activateKey = params['activateKey'];
            this.activate();
        });
    }

    activate() {
        this.uaaClient.activate_user({
            key: this.activateKey,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.status = 'success';
                } else {
                    this.status = 'fail';
                }
            }, () => {
                this.status = 'fail';
            });
    }

    goLogin() {
        this.router.navigate(['/login']);
    }

}
