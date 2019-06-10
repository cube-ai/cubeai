import {Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import {MatDialog, MatPaginator, PageEvent} from '@angular/material';
import { UserDetailsComponent } from './user-details.component';
import {UserPasswordComponent} from './user-password.component';
import {ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService, ConfirmService} from '../../shared';
import {Principal, User, UserService} from '../../account';

@Component({
    selector: 'jhi-user-mgmt',
    templateUrl: './user-management.component.html',
    styleUrls: [
        '../admin-datapage.css',
    ]
})
export class UserMgmtComponent implements OnInit, OnDestroy {

    filter = '';
    currentAccount: User;
    users: User[];
    @ViewChild(MatPaginator) paginator: MatPaginator;
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    previousItemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 1;
    previousPage = 1;
    predicate = 'id';
    reverse = true;
    toLastPage = false;

    constructor(
        private dialog: MatDialog,
        private userService: UserService,
        private principal: Principal,
        private snackBarService: SnackBarService,
        private confirmService: ConfirmService,
    ) {
    }

    ngOnInit() {
        this.currentAccount = this.principal.getCurrentAccount();
        this.loadAll();
    }

    refresh(toLastPage?: boolean) {
        this.toLastPage = toLastPage;
        this.loadAll();
    }

    ngOnDestroy() {
    }

    setActive(user, isActivated) {
        if (user.login === this.currentAccount.login) {
            this.snackBarService.error('不能去激活自己！');
            return;
        }

        if (user.login === 'admin') {
            this.snackBarService.error('不能去激活admin！');
            return;
        }

        if (user.login === 'system') {
            this.snackBarService.error('不能去激活system！');
            return;
        }

        user.activated = isActivated;

        this.userService.update(user).subscribe(
            (res) => {
                if (res.status === 200) {
                    this.loadAll();
                }
            });
    }

    loadAll() {
        this.userService.query({
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort()}).subscribe(
                (res) => this.onSuccess(res.body, res.headers),
                (res) => this.onError(res)
        );
    }

    trackIdentity(index, item: User) {
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

    private onSuccess(data, headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.users = data;

        const totalPages = Math.ceil(this.totalItems / this.itemsPerPage);
        if (this.toLastPage && this.page < totalPages) {
            this.paginator.lastPage();  // 指定跳转至最后一页，或者当前页超出页面总数（如因删除等原因），则跳转至最后一页
        } else if (this.page > totalPages) {
            this.paginator.firstPage();  // 如果当前页超出页面总数（如因删除等原因），则跳转至第一页（因此时MatPaginator跳转至最后一页有问题，所以只好跳转到第一页）
        }
    }

    private onError(error) {
        this.snackBarService.error('获取用户列表失败！');
    }

    createUser() {
        const dialogRef = this.dialog.open(UserDetailsComponent, {
            width: '800px',
            data: {
                operation: 'create',
                login: '',
                caller: this,
            },
        });
    }

    editUser(login: string) {
        const dialogRef = this.dialog.open(UserDetailsComponent, {
            width: '800px',
            data: {
                operation: 'edit',
                login,
                caller: this,
            },
        });
    }

    viewUser(login: string) {
        const dialogRef = this.dialog.open(UserDetailsComponent, {
            width: '800px',
            data: {
                operation: 'view',
                login,
                caller: this,
            },
        });
    }

    deleteUser(login: string) {
        this.confirmService.ask('确认删除该用户？').then((confirm) => {
            if (confirm) {
                this.userService.delete(login).subscribe(() => {
                    this.refresh();
                    this.snackBarService.success('删除用户成功！');
                }, () => {
                    this.snackBarService.error('删除用户失败！');
                });
            } else {
                this.snackBarService.info('放弃删除用户...');
            }
        });
    }

    resetPassword(login: string) {
        const dialogRef = this.dialog.open(UserPasswordComponent, {
            width: '500px',
            data: {
                login,
            },
        });
    }

    getOtherRoles(roles: string[]): string {
        let result = '';

        for (let i = 2; i < roles.length; i++) {
            result += (roles[i].substr(5) + '  ');
        }

        return result;
    }

}
