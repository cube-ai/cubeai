import {Component, OnInit, ViewChild} from '@angular/core';
import {ConfirmService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService} from '../../shared';
import {Principal} from '../../account';
import {TaskService} from '../service/task.service';
import {Task} from '../model/task.model';
import {MatPaginator, PageEvent} from '@angular/material';
import {Router} from '@angular/router';

@Component({
    templateUrl: './task.component.html',
    styleUrls: [
        '../ucumos-datapage.css'
    ]
})
export class TaskComponent implements OnInit {

    searchTaskStatus = 'all';

    userLogin: string;
    tasks: Task[] = [];

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
        private router: Router,
        private principal: Principal,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
        private taskService: TaskService,
    ) {
    }

    ngOnInit() {
        this.userLogin = this.principal.getCurrentAccount().login;
        this.loadAll();
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

        this.taskService.query(queryOptions).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
        );
    }

    trackIdentity(index, item: Task) {
        return item.id;
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
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

    private onSuccess(data, headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.tasks = data;
    }

    private onError(error) {
    }

    private viewTaskDetails(task: Task) {

        if (task.taskType === '模型导入') {
            this.router.navigate(['/ucumos/task-onboarding/' + task.uuid + '/' + task.taskName]);
        }

        if (task.taskType === '模型部署') {
            this.router.navigate(['/ucumos/deploy/view/' + task.uuid]);
        }
    }

}
