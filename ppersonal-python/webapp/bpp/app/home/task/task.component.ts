import {Component, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {MatPaginator, PageEvent} from '@angular/material';
import {ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {Principal, HttpService} from '../../shared';
import {Task} from '../model/task.model';

@Component({
    templateUrl: './task.component.html',
})
export class TaskComponent implements OnInit {
    userLogin: string;
    tasks: Task[];
    searchTaskStatus = 'all';

    @ViewChild(MatPaginator) paginator: MatPaginator;
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    previousItemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 1;
    previousPage = 1;
    predicate = 'id';
    reverse = false;

    constructor(
        private principal: Principal,
        private router: Router,
        private http: HttpService,
    ) {
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            if (this.userLogin) {
                this.loadAll();
            }
        });
    }

    loadAll() {
        const queryOptions = {
            userLogin: this.userLogin,
        };
        if (this.searchTaskStatus && this.searchTaskStatus !== 'all') {
            queryOptions['taskStatus'] = this.searchTaskStatus;
        }
        queryOptions['page'] = this.page - 1;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        const body = {
            action: 'get_tasks',
            args: queryOptions,
        };
        this.http.post('umm', body).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.totalItems = res.body['value']['total'];
                    this.tasks = res.body['value']['results'];
                }
            }
        );
    }

    trackIdentity(index, item: Task) {
        return item.id;
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id' + ',' + (this.reverse ? 'asc' : 'desc'));
        }
        return result;
    }

    reloadPage(pageEvent: PageEvent) {
        this.itemsPerPage = pageEvent.pageSize;
        this.page = pageEvent.pageIndex + 1;

        if (this.previousPage !== this.page) {
            this.previousPage = this.page;
            this.transition();
        }

        if (this.itemsPerPage !== this.previousItemsPerPage) {
            this.previousItemsPerPage = this.itemsPerPage;
            this.transition();
        }
    }

    transition() {
        this.refresh();
    }

    refresh() {
        this.loadAll();
    }

    viewTaskDetails(task: Task) {

        if (task.taskType === '模型导入') {
            window.location.href = '/pmodelhub/#/task-onboarding/' + task.uuid + '/' + task.taskName;
        } else if (task.taskType === '模型部署') {
            window.location.href = '/pmodelhub/#/deploy/view/' + task.uuid;
        }
    }

}
