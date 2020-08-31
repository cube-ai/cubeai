import {Component, OnInit} from '@angular/core';
import {Principal, HttpService} from '../../shared';
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
        private http: HttpService,
    ) {
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.loadAll();
        });
    }

    loadAll() {
        const body = {
            action: 'get_my_credit',
            args: {},
        };
        this.http.post('umm', body).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.credit = res.body['value'];

                    const body1 = {
                        action: 'get_credit_history',
                        args: {
                            sort: ['id,desc'],
                        },
                    };
                    this.http.post('umm', body1).subscribe(
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
