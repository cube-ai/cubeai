import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material';
import {Principal, SnackBarService, LoginService, UaaClient} from '../../shared';

@Component({
    selector: 'my-header',
    templateUrl: './header.component.html',
    styleUrls: [
        'header.css'
    ]
})
export class HeaderComponent implements OnInit {
    isMobile = window.screen.width < 960;
    newMsgCount = 0;
    predicate = 'displayOrder';
    orderAsc = false;
    idAsc = true;

    constructor(
        private principal: Principal,
        private snackBarService: SnackBarService,
        private loginService: LoginService,
        private uaaClient: UaaClient,
        private router: Router,
        public dialog: MatDialog,
    ) {
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.getNewMsgCount();

            setInterval(() => {
                this.getNewMsgCount();
            }, 600 * 1000);
        });
    }

    isAdmin(): boolean {
        return this.isAuthenticated()
            && (this.hasAuthority('ROLE_ADMIN')
                || this.hasAuthority('ROLE_CONTENT')
                || this.hasAuthority('ROLE_APPLICATION'));
    }

    getNewMsgCount() {
        if (this.principal.getLogin()) {
            this.uaaClient.get_unread_message_count({
                receiver: this.principal.getLogin(),
                deleted: false,
            }).subscribe((res) => {
                if (res.body['status'] === 'ok') {
                    this.newMsgCount = res.body['value'];
                }
            });
        }
    }

    sort() {
        const result = [this.predicate + ',' + (this.orderAsc ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id' + ',' + (this.idAsc ? 'asc' : 'desc'));
        }
        return result;
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    hasAuthority(authority: string) {
        return this.principal.hasAuthority(authority);
    }

    login() {
        const redirectUrl = (window.location.pathname + '@' + this.router.url).replace(/\//g, '$');
        window.location.href = '/#/login/' + redirectUrl;
    }

    logout() {
        if (this.principal.isAuthenticated()) {
            this.loginService.logout().subscribe(() => {
                this.principal.authenticate(null);
                this.navigateToHomepage();
            });
        } else {
            this.principal.authenticate(null);
            this.navigateToHomepage();
        }
    }

    navigateToHomepage() {
        window.location.href = '/#/';
    }

    registerUser() {
        window.location.href = '/#/register';
    }

    requestResetPassword() {
        window.location.href = '/#/passwordreset';
    }

    userSettings() {
        window.location.href = '/#/settings';
    }

    navigateToMessage() {
        window.location.href = '/#/message/msg-inbox';
    }

    navigateToHelp() {
        window.location.href = '/#/article/cubeai-help';
    }

    navigateToAboutMe() {
        window.location.href = '/#/article/cubeai-aboutme';
    }

    navigateToPersonaCenter() {
        window.location.href = '/ppersonal/#/';
    }

    navigateToAppNav() {
        window.location.href = '/#/app/平台导航';
    }

    navigateToAdmin() {
        if (!this.router.url.startsWith('/admin')) {
            window.location.href = '/#/admin';
        }
    }

}
