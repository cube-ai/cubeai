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
    currentUrl;

    startLeft: number;
    startRight: number;
    startTop: number;

    constructor(
        private router: Router,
        private principal: Principal,
    ) {}

    ngOnInit() {
        this.currentUrl = this.router.url;
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.genMenuItems();
        });
    }

    genMenuItems() {
        this.menuItems = [
            {separator: true},
            {
                label: '所有能力',
                icon: 'fa fa-fw fa-rocket',
                disabled: this.router.url === '/market',
                routerLink: ['/market'],
            },
        ];
        if (this.hasAnyRole('ROLE_USER')) {
            this.menuItems.push(
                {separator: true},
                {
                    label: '我的能力',
                    icon: 'fa fa-fw fa-plane',
                    visible: this.hasAnyRole('ROLE_USER'),
                    disabled: this.router.url === '/personal/' + this.userLogin,
                    routerLink: ['/personal/' + this.userLogin],
                },
            );
        }
        this.menuItems.push(
            {separator: true},
            {
                label: '应用示范',
                icon: 'fa fa-fw fa-image',
                disabled: this.router.url === '/demo',
                routerLink: ['/demo'],
            },
        );
        if (this.hasAnyRole('ROLE_USER')) {
            this.menuItems.push(
                {separator: true},
                {
                    label: '我的关注',
                    icon: 'fa fa-fw fa-star',
                    visible: this.hasAnyRole('ROLE_USER'),
                    url: '/ppersonal/#/star/' + this.userLogin,
                },
                {separator: true},
                {
                    label: '我的任务',
                    icon: 'fa fa-fw fa-tasks',
                    visible: this.hasAnyRole('ROLE_USER'),
                    url: '/ppersonal/#/task',
                },
            );
        }
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

    gotoDemo() {
        this.router.navigate(['/demo']);
    }

    dragStarted(event, btn, position) {
        if (position === 'left') {
            this.startLeft = parseInt(btn.style.left.replace(/px/, ''), 10);
        }
        if (position === 'right') {
            this.startRight = parseInt(btn.style.right.replace(/px/, ''), 10);
        }
        this.startTop = parseInt(btn.style.top.replace(/px/, ''), 10);
    }

    dragMoved(event, btn, position) {
        let newTop = this.startTop + event.distance.y;
        if (newTop < 40) {
            newTop = 40;
        }
        if (newTop > document.documentElement.clientHeight - 40) {
            newTop = document.documentElement.clientHeight - 40;
        }
        btn.style.top = newTop + 'px';

        let newLeft = 0;
        let newRight = 0;
        if (position === 'left') {
            newLeft = this.startLeft + event.distance.x;
            if (newLeft < 0) {
                newLeft = 0;
            }
            if (newLeft > document.documentElement.clientWidth - 40) {
                newLeft = document.documentElement.clientWidth - 40;
            }
            btn.style.left = newLeft + 'px';
        }
        if (position === 'right') {
            newRight = this.startRight - event.distance.x;
            if (newRight < 0) {
                newRight = 0;
            }
            if (newRight > document.documentElement.clientWidth - 40) {
                newRight = document.documentElement.clientWidth - 40;
            }
            btn.style.right = newRight + 'px';
        }
    }
}
