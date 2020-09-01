import {Component, OnInit} from '@angular/core';
import {Principal} from '../../shared';
import {UmmClient} from '../';
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
        private ummClient: UmmClient,
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
        this.ummClient.get_credits({}).subscribe(
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
