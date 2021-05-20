import {Component, OnInit} from '@angular/core';
import { Location } from '@angular/common';
import {ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {UmmClient} from '../service/umm_client.service';
import {Solution} from '../model/solution.model';
import {LazyLoadEvent} from 'primeng/api';
import {MessageService} from 'primeng/api';

@Component({
    templateUrl: './demo.component.html',
})
export class DemoComponent implements OnInit {
    solutions: Solution[] = [];

    filter = '';
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 0;
    predicate = 'displayOrder';
    reverse = false;
    loading = false;
    layout = 'grid';
    pagePrefix = 'ability_demo';

    constructor(
        private location: Location,
        private ummClient: UmmClient,
        private messageService: MessageService,
    ) {
    }

    ngOnInit() {
        this.restorePageState();
        this.loadData();
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

    loadData() {
        this.backupPageState();

        const queryOptions = {
            'active': true,
            'hasWeb': true,
            'deployStatus': '运行',
        };
        if (!!this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.loading = true;
        this.ummClient.get_solutions(queryOptions).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.loading = false;
                    this.totalItems = res.body['value']['total'];
                    this.solutions = res.body['value']['results'];
                } else {
                    this.loading = false;
                    this.messageService.add({severity:'error', detail:'获取应用示范列表失败！'});
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

        this.loadData();
    }

    changeLayout(event) {
        this.layout = event.layout;
        this.backupPageState();
    }

    gotoDemo(solutionUuid) {
        window.location.href = location.protocol + '//' + location.host + '/ability/web/' + solutionUuid + '/';
    }

}
