import {Component, OnInit} from '@angular/core';
import {Principal} from '../../shared';
import {UmmClient} from '../';
import {Location} from '@angular/common';
import {Credit} from '../model/credit.model';
import {CreditHistory} from '../model/credit-history.model';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    templateUrl: './credit-detail.component.html',
})
export class CreditDetailComponent implements OnInit {
    userLogin = '';
    credit: Credit;
    creditHistoryList: CreditHistory[];
    creditPlus: number;

    constructor(
        private principal: Principal,
        private route: ActivatedRoute,
        private location: Location,
        private router: Router,
        private ummClient: UmmClient,
    ) {
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.userLogin = params['userLogin'];
        });

        this.principal.updateCurrentAccount().then(() => {
            if (this.principal.hasAuthority('ROLE_ADMIN')) {
                this.loadAll();
            } else {
                this.location.back();
            }
        });
    }

    loadAll() {
        this.ummClient.get_credits({
            userLogin: this.userLogin,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok' && res.body['value'].length > 0) {
                    this.credit = res.body['value'][0];

                    this.ummClient.get_credit_history({
                        targetLogin: this.userLogin,
                        sort: ['id,desc'],
                    }).subscribe(
                        (res1) => {
                            if (res1.body['status'] === 'ok') {
                                this.creditHistoryList = res1.body['value']['results'];
                            }
                        }
                    );
                }
            }
        );
    }

    addCredit() {
        this.ummClient.update_credit({
            creditId: this.credit.id,
            creditPlus: this.creditPlus
        }).subscribe(() => {
            this.loadAll();
        });
    }

}
