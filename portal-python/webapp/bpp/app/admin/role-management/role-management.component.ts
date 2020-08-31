import {Component, OnInit} from '@angular/core';
import {SnackBarService, ConfirmService, GlobalService, HttpService} from '../../shared';

@Component({
    templateUrl: './role-management.component.html',
    styleUrls: [
        '../admin-datapage.css',
    ]
})
export class RoleMgmtComponent implements OnInit {

    sysRoles = ['ROLE_ADMIN', 'ROLE_USER', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_DEVELOPER'];
    roles: string[];
    role: string;

    constructor(
        public globalService: GlobalService,
        private http: HttpService,
        private snackBarService: SnackBarService,
        private confirmService: ConfirmService,
    ) {
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }

        this.refresh();
    }

    refresh() {
        const body = {
            action: 'get_authorities',
            args: {},
        };
        this.http.post('uaa', body).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.roles = res.body['value'];
                }
            }
        );
    }

    isSysRole(role: string): boolean {
        return this.sysRoles.includes(role);
    }

    createRole() {
        if (this.roles.includes(this.role)) {
            this.snackBarService.error('角色已存在，不能重复添加！');
            return;
        }

        if (!this.role.startsWith('ROLE_')) {
            this.snackBarService.error('角色必须以“ROLE_”开头！');
            return;
        }

        const body = {
            action: 'create_authority',
            args: {
                authority: this.role,
            },
        };
        this.http.post('uaa', body).subscribe(
            (res) => {
                this.refresh();
            }
        );
    }

    deleteRole(role: string) {
        this.confirmService.ask('确定要删除该角色？').then((confirm) => {
            if (confirm) {
                const body = {
                    action: 'delete_authority',
                    args: {
                        authority: role,
                    },
                };
                this.http.post('uaa', body).subscribe(
                    (res) => {
                        this.refresh();
                        if (res.body['status'] !== 'ok') {
                            this.snackBarService.error('删除角色失败！可能是已有用户使用该角色。');
                        }
                    }, () => {
                        this.refresh();
                        this.snackBarService.error('删除角色失败！可能是已有用户使用该角色。');
                    }
                );
            }
        });
    }

}
