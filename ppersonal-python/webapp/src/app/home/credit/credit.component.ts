import {Component, OnInit} from '@angular/core';
import {Principal} from '../../shared';
import {UmmClient} from '../service/umm_client.service';
import {ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {Credit} from '../model/credit.model';
import {CreditHistory} from '../model/credit-history.model';
import {LazyLoadEvent} from 'primeng/api';
import {MessageService} from 'primeng/api';

@Component({
    templateUrl: './credit.component.html',
})
export class CreditComponent implements OnInit {
    credit: Credit;
    creditHistoryList: CreditHistory[];

    filter = '';
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 0;
    predicate = 'id';
    reverse = false;
    loading = false;
    layout = 'list';
    pagePrefix = 'mycredit';

    constructor(
        private principal: Principal,
        private ummClient: UmmClient,
        private messageService: MessageService,
    ) {
    }

    ngOnInit() {
        this.restorePageState();
        this.loadCredit();
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
        this.ummClient.get_my_credit({}).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.credit = res.body['value'];
                }
            }
        );
    }

    loadData() {
        const queryOptions = {};
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

}
