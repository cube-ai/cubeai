import {Component, OnInit} from '@angular/core';
import { GlobalService, UaaClient, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {Application} from '../../shared/model/application.model';
import {Router} from '@angular/router';
import {Principal, User} from '../../shared';
import { LazyLoadEvent } from 'primeng/api';
import {MessageService} from 'primeng/api';
import {ConfirmationService} from 'primeng/api';

@Component({
    templateUrl: './application.component.html',
})
export class ApplicationComponent implements OnInit {
    user: User;
    applications: Application[] = [];

    filter = '';
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 0;
    predicate = 'id';
    reverse = false;
    loading = false;

    constructor(
        public globalService: GlobalService,
        private router: Router,
        private principal: Principal,
        private uaaClient: UaaClient,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
    ) {
    }

    ngOnInit() {
        this.user = this.principal.getCurrentAccount();
    }

    loadData() {
        const queryOptions = {};
        if (this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.loading = true;
        this.uaaClient.get_applications(queryOptions).subscribe(
            (res) => {
                this.loading = false;
                if (res.body['status'] === 'ok') {
                    this.totalItems = res.body['value']['total'];
                    this.applications = res.body['value']['results'];
                } else {
                    this.messageService.add({severity:'error', detail:'获取应用列表失败！'});
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
        this.confirmationService.confirm({
            target: event.target,
            message: '确定要删除该应用： ' + application.name + ' ？',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: '是',
            rejectLabel: '否',
            accept: () => {
                this.uaaClient.delete_application({
                    id: application.id,
                }).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.loadData();
                        } else {
                            this.messageService.add({severity:'error', detail:'删除失败！'});
                        }
                    }, () => {
                        this.messageService.add({severity:'error', detail:'网络或服务器故障'});
                    }
                );
            },
            reject: () => {}
        });
    }

}
