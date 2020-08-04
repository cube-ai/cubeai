import {Component, OnInit} from '@angular/core';
import {Principal} from '../../shared';
import {Location} from '@angular/common';
import {Credit} from '../model/credit.model';
import {CreditService} from '../service/credit.service';
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
        private creditService: CreditService,
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
        this.creditService.queryAllCredits({}).subscribe(
            (res) => {
                this.credits = res.body;
            }
        );
    }

    gotoCredit(userLogin: string) {
        this.router.navigate(['/creditdetail/' + userLogin]);
    }

}
