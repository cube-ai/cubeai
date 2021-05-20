import {Component, AfterViewInit, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {GlobalService, Principal} from '../shared';

@Component({
    templateUrl: './message.component.html',
})
export class MessageComponent implements AfterViewInit, OnInit {
    sideNavStatus = {
        'openMenu': true,
        'openPopup': false,
    };
    menuItems;

    constructor(
        private principal: Principal,
        private router: Router,
        private globalService: GlobalService,
    ) {
    }

    ngAfterViewInit() {
        this.globalService.setSideNavStatus(this.sideNavStatus);
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.menuItems = [{
                label: '<h2>站内消息</h2>',
                escape: false,
                items: [
                    {
                        separator: true,
                    },
                    {
                        label: '收信箱',
                        icon: 'fa fa-fw fa-inbox',
                        routerLink: ['msg-inbox'],
                        command: () => {
                            this.sideNavStatus['openPopup'] = false;
                        }
                    },
                    {
                        separator: true,
                    },
                    {
                        label: '已发送',
                        icon: 'fa fa-fw fa-mars-stroke',
                        routerLink: ['msg-sent'],
                        command: () => {
                            this.sideNavStatus['openPopup'] = false;
                        }
                    },
                    {
                        separator: true,
                    },
                    {
                        label: '已删除',
                        icon: 'fa fa-fw fa-trash-o',
                        routerLink: ['msg-deleted'],
                        command: () => {
                            this.sideNavStatus['openPopup'] = false;
                        }
                    },
                ]
            }];

            if (this.router.url === '/message') {
                this.router.navigate(['/message/msg-inbox']);
            }
        });
    }

    hasAuthority(authority: string) {
        return this.principal.hasAuthority(authority);
    }

}
