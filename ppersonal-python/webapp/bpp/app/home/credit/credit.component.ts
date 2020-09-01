import {Component, OnInit} from '@angular/core';
import {Principal} from '../../shared';
import {UmmClient} from '../';
import {Location} from '@angular/common';
import {Credit} from '../model/credit.model';
import {CreditHistory} from '../model/credit-history.model';
import {Router} from '@angular/router';

@Component({
    templateUrl: './credit.component.html',
})
export class CreditComponent implements OnInit {
    userLogin = '';
    credit: Credit;
    creditHistoryList: CreditHistory[];

    constructor(
        private principal: Principal,
        private location: Location,
        private router: Router,
        private ummClient: UmmClient,
    ) {
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.loadAll();
        });
    }

    loadAll() {
        this.ummClient.get_my_credit({}).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.credit = res.body['value'];

                    this.ummClient.get_credit_history({
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

}
