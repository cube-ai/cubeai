import { Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpService} from '../../shared';

@Component({
    templateUrl: './activate.component.html'
})
export class ActivateComponent implements OnInit {

    status = '';
    activateKey: string;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private http: HttpService,
    ) {
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.activateKey = params['activateKey'];
            this.activate();
        });
    }

    activate() {
        const body = {
            action: 'activate_user',
            args: {
                key: this.activateKey,
            },
        };
        this.http.post('uaa', body).subscribe(
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
