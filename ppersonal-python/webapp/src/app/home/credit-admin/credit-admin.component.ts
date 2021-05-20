import {Component, OnInit} from '@angular/core';
import {Location} from '@angular/common';
import {Router} from '@angular/router';
import {Principal} from '../../shared';
import {UmmClient} from '../service/umm_client.service';
import {Credit} from '../model/credit.model';
import {MessageService} from 'primeng/api';

@Component({
    templateUrl: './credit-admin.component.html',
})
export class CreditAdminComponent implements OnInit {
    credits: Credit[];
    loading = false;

    constructor(
        private principal: Principal,
        private location: Location,
        private router: Router,
        private ummClient: UmmClient,
        private messageService: MessageService,
    ) {
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            if (this.principal.hasAuthority('ROLE_ADMIN')) {
                this.loadData();
            } else {
                this.location.back();
            }
        });
    }

    loadData() {
        this.loading = true;
        this.ummClient.get_credits({}).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.loading = false;
                    this.credits = res.body['value'];
                } else {
                    this.loading = false;
                    this.messageService.add({severity:'error', detail:'获取用户积分列表失败！'});
                }
            }, () => {
                this.loading = false;
                this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
            });
    }

    gotoCredit(userLogin: string) {
        this.router.navigate(['/creditdetail/' + userLogin]);
    }

}
