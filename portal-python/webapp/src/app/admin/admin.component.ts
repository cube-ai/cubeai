import {Component, AfterViewInit, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {GlobalService, Principal} from '../shared';

@Component({
    templateUrl: './admin.component.html',
})
export class AdminComponent implements AfterViewInit, OnInit {
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
            this.genMenuItems();

            if (this.router.url === '/admin') {
                if (this.hasAuthority('ROLE_ADMIN')) {
                    this.router.navigate(['/admin/user-management']);
                    this.disableMenuItem('user-management');
                } else if (this.hasAuthority('ROLE_APPLICATION')) {
                    this.router.navigate(['/admin/application']);
                    this.disableMenuItem('application');
                } else if (this.hasAuthority('ROLE_CONTENT')) {
                    this.router.navigate(['/admin/articles']);
                    this.disableMenuItem('articles');
                }
            }
        });
    }

    genMenuItems() {
        this.menuItems = [{
            label: '<h2>系统管理</h2>',
            escape: false,
            items: [],
        }];
        if (this.hasAuthority('ROLE_ADMIN')) {
            this.menuItems[0].items.push(
                {separator: true},
                {
                    label: '用户管理',
                    icon: 'fa fa-fw fa-user',
                    disabled: this.router.url.startsWith('/admin/user-management'),
                    routerLink: ['user-management'],
                    command: () => {
                        this.sideNavStatus['openPopup'] = false;
                        this.genMenuItems();
                        this.disableMenuItem('user-management');
                    }
                },
                {separator: true},
                {
                    label: '角色管理',
                    icon: 'fa fa-fw fa-leaf',
                    disabled: this.router.url.startsWith('/admin/role-management'),
                    routerLink: ['role-management'],
                    command: () => {
                        this.sideNavStatus['openPopup'] = false;
                        this.genMenuItems();
                        this.disableMenuItem('role-management');
                    }
                },
            );
        }
        if (this.hasAuthority('ROLE_APPLICATION')) {
            this.menuItems[0].items.push(
                {separator: true},
                {
                    label: '应用管理',
                    icon: 'fa fa-fw fa-cubes',
                    disabled: this.router.url.startsWith('/admin/application'),
                    routerLink: ['application'],
                    command: () => {
                        this.sideNavStatus['openPopup'] = false;
                        this.genMenuItems();
                        this.disableMenuItem('application');
                    }
                },
            );
        }
        if (this.hasAuthority('ROLE_CONTENT')) {
            this.menuItems[0].items.push(
                {separator: true},
                {
                    label: '文稿管理',
                    icon: 'fa fa-fw fa-book',
                    disabled: this.router.url.startsWith('/admin/articles'),
                    routerLink: ['articles'],
                    command: () => {
                        this.sideNavStatus['openPopup'] = false;
                        this.genMenuItems();
                        this.disableMenuItem('articles');
                    }
                },
                {separator: true},
                {
                    label: '附件管理',
                    icon: 'fa fa-fw fa-paperclip',
                    disabled: this.router.url.startsWith('/admin/attachment'),
                    routerLink: ['attachment'],
                    command: () => {
                        this.sideNavStatus['openPopup'] = false;
                        this.genMenuItems();
                        this.disableMenuItem('attachment');
                    }
                },
            );
        }
        if (this.hasAuthority('ROLE_ADMIN')) {
            this.menuItems[0].items.push(
                {separator: true},
                {
                    label: '任务管理',
                    icon: 'fa fa-fw fa-tasks',
                    visible: this.hasAuthority('ROLE_ADMIN'),
                    url: ['/ppersonal/#/taskadmin'],
                    command: () => {
                        this.sideNavStatus['openPopup'] = false;
                    }
                },
                {separator: true},
                {
                    label: '积分管理',
                    icon: 'fa fa-fw fa-credit-card',
                    visible: this.hasAuthority('ROLE_ADMIN'),
                    url: ['/ppersonal/#/creditadmin'],
                    command: () => {
                        this.sideNavStatus['openPopup'] = false;
                    }
                },
            );
        }
    }

    disableMenuItem(routerLink: string) {
        this.menuItems[0]['items'].forEach((item) => {
            if (!!item['routerLink']) {
                item['disabled'] = item['routerLink'][0] === routerLink;
            }
        })
    }

    hasAuthority(authority: string) {
        return this.principal.hasAuthority(authority);
    }

}
