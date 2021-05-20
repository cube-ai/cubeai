import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {Principal, GlobalService, LoginService, UaaClient} from '../../shared';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
})
export class HeaderComponent implements OnInit {
    predicate = 'displayOrder';
    orderAsc = false;
    idAsc = true;

    websocket: WebSocket;
    newMsgCount = 0;

    constructor(
        private principal: Principal,
        private globalService: GlobalService,
        private loginService: LoginService,
        private uaaClient: UaaClient,
        private router: Router,
    ) {
        this.globalService.setHeader(this);
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            if (this.principal.isAuthenticated()) {
                this.refreshUnreadMsgCount();
                if (!this.principal.getCurrentAccount().imageUrl) {
                    this.uaaClient.get_random_avatar({
                        size: 200,
                    }).subscribe();
                }
            }
        });
    }

    getAccountName() {
        return this.isAuthenticated() ? this.principal.getFullName() || this.principal.getLogin() : '登录';
    }

    getAccountMenu() {
        if (this.isAuthenticated()) {
            return [
                {label: '我的帐号', icon: 'pi pi-id-card', command: () => {
                        this.gotoUserSettings();
                    }},
                {separator: true},
                {label: '个人中心', icon: 'pi pi-calendar', command: () => {
                        this.gotoPersonaCenter();
                    }},
                {separator: true},
                {label: '退出', icon: 'pi pi-sign-out', command: () => {
                        this.logout();
                    }},
            ];
        }

        return [
            {label: '登录', icon: 'pi pi-sign-in', command: () => {
                    this.login();
                }},
            {separator: true},
            {label: '注册', icon: 'pi pi-user-plus', command: () => {
                    this.gotoRegisterUser();
                }},
            {separator: true},
            {label: '忘记密码', icon: 'pi pi-key', command: () => {
                    this.gotoResetPassword();
                }},
        ];
    }

    refreshUnreadMsgCount() {
        this.getNewMsgCount();
        this.connectWebSocket();
        // setInterval(() => {
        //     this.getNewMsgCount();
        // }, 600 * 1000);
    }

    connectWebSocket() {
        if (this.websocket != null) {
            this.websocket.close();
        }

        this.websocket = new WebSocket((window.location.protocol).replace(/http/, 'ws') + '//' + window.location.host + '/websocket');

        const that = this;

        this.websocket.onopen = () => {
            that.websocket.send(JSON.stringify({
                'type': 'subscribe',
                'content': 'message_' + this.principal.getLogin(),
            }));
        };

        this.websocket.onmessage = (event) => {
            const msg = JSON.parse(event.data);
            if (msg['type'] === 'data') {
                this.newMsgCount = JSON.parse(event.data)['content']['unread_msgs'];
            }
        };
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

    isAdmin(): boolean {
        return this.isAuthenticated()
            && (this.hasAuthority('ROLE_ADMIN')
                || this.hasAuthority('ROLE_CONTENT')
                || this.hasAuthority('ROLE_APPLICATION'));
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
        window.localStorage.setItem('loginRedirectUrl', window.location.pathname + '#' + this.router.url);
        window.localStorage.setItem('loginReason', '');
        window.location.href = '/#/login';
    }

    logout() {
        if (this.principal.isAuthenticated()) {
            this.loginService.logout().subscribe(() => {
                this.principal.authenticate(null);
                this.gotoHomepage();
            });
        } else {
            this.principal.authenticate(null);
            this.gotoHomepage();
        }

        if (this.websocket != null) {
            this.websocket.close();
        }
    }

    gotoAccount() {
        if (this.isAuthenticated()) {
            this.gotoUserSettings();
        } else {
            this.login();
        }
    }

    gotoRegisterUser() {
        window.location.href = '/#/register';
    }

    gotoResetPassword() {
        window.location.href = '/#/passwordreset';
    }

    gotoUserSettings() {
        window.location.href = '/#/settings';
    }

    gotoMessage() {
        window.location.href = '/#/message/msg-inbox';
    }

    gotoHelp() {
        window.location.href = '/#/article/cubeai-help';
    }

    gotoAboutMe() {
        window.location.href = '/#/article/cubeai-aboutme';
    }

    gotoPersonaCenter() {
        window.location.href = '/ppersonal/#/';
    }

    gotoAppNav() {
        window.location.href = '/#/app/平台导航';
    }

    gotoAdmin() {
        if (!this.router.url.startsWith('/admin')) {
            window.location.href = '/#/admin';
        }
    }

    gotoHomepage() {
        window.location.href = '/#/';
    }

}
