import {Component, OnInit} from '@angular/core';
import {Principal} from '../../shared';
import {Location} from '@angular/common';
import {Credit} from '../model/credit.model';
import {CreditService} from '../service/credit.service';
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
        private creditService: CreditService,
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
        this.creditService.queryAllCredits({
            userLogin: this.userLogin,
        }).subscribe(
            (res) => {
                this.credit = res.body[0];

                this.creditService.queryCreditHistory({
                    targetLogin: this.userLogin,
                    sort: ['id,desc']
                }).subscribe(
                    (res1) => {
                        this.creditHistoryList = res1.body;
                    }
                );
            }
        );
    }

    addCredit() {
        this.creditService.update(this.credit.id, this.creditPlus).subscribe(() => {
            this.loadAll();
        });
    }

}
