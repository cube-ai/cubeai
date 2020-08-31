import {Component, OnInit} from '@angular/core';
import {Principal, HttpService} from '../../shared';
import {Location} from '@angular/common';
import {Credit} from '../model/credit.model';
import {Router} from '@angular/router';

@Component({
    templateUrl: './credit-admin.component.html',
})
export class CreditAdminComponent implements OnInit {
    credits: Credit[];

    constructor(
        private principal: Principal,
        private location: Location,
        private router: Router,
        private http: HttpService,
    ) {
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            if (this.principal.hasAuthority('ROLE_ADMIN')) {
                this.loadAll();
            } else {
                this.location.back();
            }
        });
    }

    loadAll() {
        const body = {
            action: 'get_credits',
            args: {},
        };
        this.http.post('umm', body).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.credits = res.body['value'];
                }
            }
        );
    }

    gotoCredit(userLogin: string) {
        this.router.navigate(['/creditdetail/' + userLogin]);
    }

}
