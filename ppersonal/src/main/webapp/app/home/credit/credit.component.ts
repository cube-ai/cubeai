import {Component, OnInit} from '@angular/core';
import {Principal} from '../../shared';
import {Location} from '@angular/common';
import {Credit} from '../model/credit.model';
import {CreditService} from '../service/credit.service';
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
        private creditService: CreditService,
    ) {
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.loadAll();
        });
    }

    loadAll() {
        this.creditService.queryCredit().subscribe(
            (res) => {
                this.credit = res.body;

                this.creditService.queryCreditHistory({
                    sort: ['id,desc']
                }).subscribe(
                    (res1) => {
                        this.creditHistoryList = res1.body;
                    }
                );
            }
        );
    }

}
