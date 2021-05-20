import {Component, OnInit} from '@angular/core';
import {Principal} from '../../shared';
import {UmmClient} from '../service/umm_client.service';
import {ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {Location} from '@angular/common';
import {Credit} from '../model/credit.model';
import {CreditHistory} from '../model/credit-history.model';
import {ActivatedRoute, Router} from '@angular/router';
import {LazyLoadEvent} from 'primeng/api';
import {MessageService} from 'primeng/api';

@Component({
    templateUrl: './credit-detail.component.html',
})
export class CreditDetailComponent implements OnInit {
    userLogin = '';
    credit: Credit;
    creditHistoryList: CreditHistory[];
    creditPlus = 0;

    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 0;
    predicate = 'id';
    reverse = false;
    loading = false;
    layout = 'list';
    pagePrefix = 'creditdetail';

    constructor(
        private principal: Principal,
        private route: ActivatedRoute,
        private location: Location,
        private router: Router,
        private ummClient: UmmClient,
        private messageService: MessageService,
    ) {
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.userLogin = params['userLogin'];
            this.pagePrefix = 'creditdetail_' + this.userLogin;
            this.restorePageState();

            this.principal.updateCurrentAccount().then(() => {
                if (!this.principal.hasAuthority('ROLE_ADMIN')) {
                    this.location.back();
                }

                this.loadCredit();
            });
        });
    }

    restorePageState() {
        let temp = window.localStorage.getItem(this.pagePrefix + '_itemsPerPage');
        if (temp !== null) {
            this.itemsPerPage = parseInt(temp, 10);
        }

        temp = window.localStorage.getItem(this.pagePrefix + '_page');
        if (temp !== null) {
            this.page = parseInt(temp, 10);
        }

        temp = window.localStorage.getItem(this.pagePrefix + '_predicate');
        if (temp !== null) {
            this.predicate = temp;
        }

        temp = window.localStorage.getItem(this.pagePrefix + '_reverse');
        if (temp !== null) {
            this.reverse = temp === 'true';
        }

        temp = window.localStorage.getItem(this.pagePrefix + '_layout');
        if (temp !== null) {
            this.layout = temp;
        }
    }

    backupPageState() {
        window.localStorage.setItem(this.pagePrefix + '_itemsPerPage', this.itemsPerPage.toString(10));
        window.localStorage.setItem(this.pagePrefix + '_page', this.page.toString(10));
        window.localStorage.setItem(this.pagePrefix + '_predicate', this.predicate);
        window.localStorage.setItem(this.pagePrefix + '_reverse', this.reverse.toString());
        window.localStorage.setItem(this.pagePrefix + '_layout', this.layout);
    }

    loadCredit() {
        this.ummClient.get_credits({
            userLogin: this.userLogin,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok' && res.body['value'].length > 0) {
                    this.credit = res.body['value'][0];
                }
            }
        );
    }

    loadData() {
        const queryOptions = {};
        queryOptions['targetLogin'] = this.userLogin;
        queryOptions['page'] = this.page;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.loading = true;
        this.ummClient.get_credit_history(queryOptions).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.loading = false;
                    this.totalItems = res.body['value']['total'];
                    this.creditHistoryList = res.body['value']['results'];
                } else {
                    this.loading = false;
                    this.messageService.add({severity:'error', detail:'获取积分历史数据失败！'});
                }
            }, () => {
                this.loading = false;
                this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
            });
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id' + ',' + (this.reverse ? 'asc' : 'desc'));
        }
        return result;
    }

    reloadPage(event: LazyLoadEvent) {
        if (event.rows) {
            this.itemsPerPage = event.rows;
            this.page = event.first / event.rows;
        }

        if (event.sortField) {
            this.predicate = event.sortField;
            this.reverse = event.sortOrder > 0;
        }

        this.backupPageState();
        this.loadData();
    }

    changeLayout(event) {
        this.layout = event.layout;
        this.backupPageState();
    }

    addCredit() {
        this.ummClient.update_credit({
            creditId: this.credit.id,
            creditPlus: this.creditPlus
        }).subscribe(() => {
            this.loadCredit();
            this.loadData();
        });
    }

}
