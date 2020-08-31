import {Component, OnInit} from '@angular/core';
import {Principal, HttpService} from '../../shared';
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
        private http: HttpService,
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
        const body = {
            action: 'get_credits',
            args: {
                userLogin: this.userLogin,
            },
        };
        this.http.post('umm', body).subscribe(
            (res) => {
                if (res.body['status'] === 'ok' && res.body['value'].length > 0) {
                    this.credit = res.body['value'][0];

                    const body1 = {
                        action: 'get_credit_history',
                        args: {
                            targetLogin: this.userLogin,
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

    addCredit() {
        const body = {
            action: 'update_credit',
            args: {
                creditId: this.credit.id,
                creditPlus: this.creditPlus
            },
        };
        this.http.post('umm', body).subscribe(() => {
            this.loadAll();
        });
    }

}
