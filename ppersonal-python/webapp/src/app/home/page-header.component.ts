import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Principal} from '../shared';

@Component({
    selector: 'app-pageheader',
    templateUrl: './page-header.component.html',
})
export class PageHeaderComponent implements OnInit  {
    userLogin: string;
    menuItems;

    startLeft: number;
    startTop: number;

    constructor(
        private router: Router,
        private principal: Principal,
    ) {}

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.menuItems = [
                {
                    label: '我的模型',
                    icon: 'fa fa-fw fa-cubes',
                    visible: this.hasAnyRole('ROLE_USER'),
                    url: ['/pmodelhub/#/personal/' + this.userLogin],
                },
                {
                    separator: this.hasAnyRole('ROLE_USER'),
                },
                {
                    label: '我的能力',
                    icon: 'fa fa-fw fa-plane',
                    visible: this.hasAnyRole('ROLE_USER'),
                    url: ['/popen/#/personal/' + this.userLogin],
                },
                {
                    separator: this.hasAnyRole('ROLE_USER'),
                },
                {
                    label: '我的关注',
                    icon: 'fa fa-fw fa-star',
                    visible: this.hasAnyRole('ROLE_USER'),
                    disabled: this.router.url === '/star/' + this.userLogin,
                    routerLink: ['/star/' + this.userLogin],
                },
                {
                    separator: this.hasAnyRole('ROLE_USER'),
                },
                {
                    label: '我的任务',
                    icon: 'fa fa-fw fa-tasks',
                    visible: this.hasAnyRole('ROLE_USER'),
                    disabled: this.router.url === '/task',
                    routerLink: ['/task'],
                },
                {
                    separator: this.hasAnyRole('ROLE_USER'),
                },
                {
                    label: '我的积分',
                    icon: 'fa fa-fw fa-credit-card',
                    visible: this.hasAnyRole('ROLE_USER'),
                    disabled: this.router.url === '/credit',
                    routerLink: ['/credit'],
                },
            ];
        });
    }

    hasAnyRole(roles: string) {
        if (!roles) {
            return true;
        }
        return this.principal.hasAnyAuthority(roles.split(','));
    }

    gotoHome() {
        this.router.navigate(['/']);
    }

    dragStarted(event, btn) {
        this.startLeft = parseInt(btn.style.left.replace(/px/, ''), 10);
        this.startTop = parseInt(btn.style.top.replace(/px/, ''), 10);
    }

    dragMoved(event, btn) {
        let newLeft = this.startLeft + event.distance.x;
        let newTop = this.startTop + event.distance.y;

        if (newLeft < 0) {
            newLeft = 0;
        }
        if (newLeft > document.documentElement.clientWidth - 40) {
            newLeft = document.documentElement.clientWidth - 40;
        }
        if (newTop < 40) {
            newTop = 40;
        }
        if (newTop > document.documentElement.clientHeight - 40) {
            newTop = document.documentElement.clientHeight - 40;
        }

        btn.style.left = newLeft + 'px';
        btn.style.top = newTop + 'px';
    }
}
