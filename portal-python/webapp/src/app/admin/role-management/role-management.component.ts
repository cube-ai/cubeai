import {Component, OnInit} from '@angular/core';
import {GlobalService, UaaClient} from '../../shared';
import {MessageService} from 'primeng/api';
import {ConfirmationService} from 'primeng/api';

@Component({
    templateUrl: './role-management.component.html',
})
export class RoleMgmtComponent implements OnInit {

    sysRoles = ['ROLE_ADMIN', 'ROLE_USER', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_DEVELOPER'];
    roles: string[];
    role: string;

    constructor(
        public globalService: GlobalService,
        private uaaClient: UaaClient,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
    ) {
    }

    ngOnInit() {
        this.refresh();
    }

    refresh() {
        this.uaaClient.get_authorities({}).subscribe(
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
        this.role = this.role.toUpperCase();
        if (this.roles.includes(this.role)) {
            this.messageService.add({severity:'error', detail:'角色已存在，不能重复添加！'});
            return;
        }

        if (!this.role.startsWith('ROLE_')) {
            this.messageService.add({severity:'error', detail:'角色必须以“ROLE_”开头！'});
            return;
        }

        this.uaaClient.create_authority({
            authority: this.role,
        }).subscribe(
            (res) => {
                this.refresh();
            }
        );
    }

    deleteRole(role: string, event: Event) {
        this.confirmationService.confirm({
            target: event.target,
            message: '确定要删除该角色？',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: '是',
            rejectLabel: '否',
            accept: () => {
                this.uaaClient.delete_authority({
                    authority: role,
                }).subscribe(
                    (res) => {
                        this.refresh();
                        if (res.body['status'] !== 'ok') {
                            this.messageService.add({severity:'error', detail:'删除角色失败！'});
                        }
                    }, () => {
                        this.refresh();
                        this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
                    }
                );
            },
            reject: () => {}
        });
    }

}
