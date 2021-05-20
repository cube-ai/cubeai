import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, GlobalService} from '../../shared';
import {Principal, User, UaaClient} from '../../shared';
import {LazyLoadEvent} from 'primeng/api';
import {MessageService} from 'primeng/api';
import {ConfirmationService} from 'primeng/api';

export class Authority {
    name: string;
    selected: Boolean;

    constructor(name?: string,
                selected?: Boolean,
    ) {
        this.name = name ? name : null;
        this.selected = selected;
    }
}

@Component({
    templateUrl: './user-management.component.html',
})
export class UserMgmtComponent implements OnInit {

    filter = '';
    currentAccount: User;
    users: User[];
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 0;
    predicate = 'id';
    reverse = true;
    loading = false;
    selectedUser: User;
    menuItems;
    authorities = [];

    constructor(
        private router: Router,
        public globalService: GlobalService,
        private uaaClient: UaaClient,
        private principal: Principal,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
    ) {
    }

    ngOnInit() {
        this.currentAccount = this.principal.getCurrentAccount();
        this.uaaClient.get_authorities({}).subscribe(
            (res1) => {
                if (res1.body['status'] === 'ok') {
                    this.authorities = res1.body['value'];
                }
            }
        );
    }

    loadData() {
        this.loading = true;
        this.uaaClient.get_users({
            filter: this.filter,
            page: this.page,
            size: this.itemsPerPage,
            sort: this.sort()
        }).subscribe(
            (res) => {
                this.loading = false;
                if (res.body['status'] === 'ok') {
                    this.totalItems = res.body['value']['total'];
                    this.users = res.body['value']['results'];
                } else {
                    this.messageService.add({severity:'error', detail:'获取用户列表失败！'});
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

    genMenuItems() {
        this.menuItems = [
            {
                label: '查看',
                icon: 'pi pi-fw pi-eye',
                command: () => {
                    this.viewUser(this.selectedUser.login);
                }
            },
        ];
        if (this.selectedUser.login !== 'admin') {
            this.menuItems.push(
                {
                    label: '编辑',
                    icon: 'pi pi-fw pi-pencil',
                    command: () => {
                        this.editUser(this.selectedUser.login);
                    }
                },
            );
        }
        if (this.selectedUser.login !== 'admin' && this.selectedUser.login !== this.currentAccount.login) {
            this.menuItems.push(
                {
                    label: '删除',
                    icon: 'pi pi-fw pi-trash',
                    command: () => {
                        this.deleteUser(this.selectedUser.login);
                    }
                },
            );
        }
        if (this.selectedUser.login !== 'admin') {
            this.menuItems.push(
                {
                    label: '修改密码',
                    icon: 'pi pi-fw pi-key',
                    command: () => {
                        this.resetPassword(this.selectedUser.login);
                    }
                },
            );
        }
        this.menuItems.push(
            {
                label: '新建用户',
                icon: 'pi pi-fw pi-user-plus',
                command: () => {
                    this.createUser();
                }
            },
        );
    }

    createUser() {
        this.router.navigate(['/admin/user-detail/create/new']);
    }

    editUser(login: string) {
        if (login === 'admin') {
            this.messageService.add({severity: 'error', detail: 'admin不可编辑！'});
            return;
        }
        this.router.navigate(['/admin/user-detail/edit/' + login]);
    }

    viewUser(login: string) {
        this.router.navigate(['/admin/user-detail/view/' + login]);
    }

    resetPassword(login: string) {
        if (login === 'admin') {
            this.messageService.add({severity:'error', detail:'admin密码不得修改！'});
            return;
        }
        this.router.navigate(['/admin/user-detail/password/' + login]);
    }

    deleteUser(login: string) {
        if (login === 'admin') {
            this.messageService.add({severity:'error', detail:'admin不可删除！'});
            return;
        }
        if (login === this.currentAccount.login) {
            this.messageService.add({severity:'error', detail:'不能删除自己！'});
            return;
        }

        this.confirmationService.confirm({
            message: '确定要删除该用户： ' + login + ' ？',
            accept: () => {
                this.uaaClient.delete_user({
                    login,
                }).subscribe((res) => {
                    if (res.body['status'] === 'ok') {
                        this.loadData();
                        this.messageService.add({severity:'success', detail:'成功删除用户！'});
                    } else {
                        this.messageService.add({severity:'error', detail:'删除用户失败！'});
                    }
                }, () => {
                    this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
                });
            },
            reject: () => {}
        });

    }

    setActive(user) {
        if (user.login === this.currentAccount.login) {
            this.messageService.add({severity:'error', detail:'不能去激活自己！'});
            return;
        }

        if (user.login === 'admin') {
            this.messageService.add({severity:'error', detail:'不能去激活admin！'});
            return;
        }

        this.uaaClient.update_user({
            user,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.loadData();
                }
            });
    }

    getUserRoles(user) {
        const roles = [];
        user.authorities.forEach((authority) => {
            roles.push(authority.substr(5));
        });
        return roles;
    }

    getOtherRoles(roles: string[]): string {
        let result = '';

        for (let i = 2; i < roles.length; i++) {
            result += (roles[i] + '  ');
        }

        return result;
    }

}
