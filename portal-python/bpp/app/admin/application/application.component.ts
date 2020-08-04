import {Component, OnInit, ViewChild} from '@angular/core';
import {ConfirmService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService, GlobalService, ApplicationService} from '../../shared';
import {Application} from '../../shared/model/application.model';
import {Router} from '@angular/router';
import {Principal, User} from '../../shared';
import {MatPaginator, PageEvent} from '@angular/material';

@Component({
    templateUrl: './application.component.html',
    styleUrls: [
        '../admin-datapage.css'
    ]
})
export class ApplicationComponent implements OnInit {
    user: User;
    applications: Application[] = [];

    filter = '';
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
        public globalService: GlobalService,
        private router: Router,
        private principal: Principal,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
        private applicationService: ApplicationService,
    ) {
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }

        this.user = this.principal.getCurrentAccount();
        this.loadAll();
    }

    loadAll() {
        const queryOptions = {};
        if (this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page - 1;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.applicationService.query(queryOptions).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
        );
    }

    trackIdentity(index, item: Application) {
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
        this.applications = data;
    }

    private onError(error) {
    }

    canCreate(): boolean {
        return this.user.authorities.includes('ROLE_APPLICATION');
    }

    canEdit(application: Application): boolean {
        return this.user.authorities.includes('ROLE_APPLICATION');
    }

    canDelete(application: Application): boolean {
        return this.user.authorities.includes('ROLE_APPLICATION');
    }

    createApplication() {
        this.router.navigate(['/admin/appdetail/create/0']);
    }

    viewApplication(application: Application) {
        this.router.navigate(['/admin/appdetail/view/' + application.id]);
    }

    editApplication(application: Application) {
        this.router.navigate(['/admin/appdetail/edit/' + application.id]);
    }

    deleteApplication(application: Application) {
        this.confirmService.ask('确定要删除该应用？').then((confirm) => {
            if (confirm) {
                this.applicationService.delete(application.id).subscribe(
                    () => {
                        this.refresh();
                    }, () => {
                        this.snackBarService.error('删除失败！可能是没有权限...');
                    }
                );
            }
        });
    }

}
