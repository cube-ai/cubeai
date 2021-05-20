import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Principal, UaaClient} from '../shared';

@Component({
    templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {

    userLogin: string;
    apps = [];

    constructor(
        private router: Router,
        private principal: Principal,
        private uaaClient: UaaClient,
    ) {}

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.apps = [
                {
                    name: '所有模型',
                    url: '/market',
                    picture: null,
                    needRoles: '',
                },
                {
                    name: '我的模型',
                    url: '/personal/' + this.userLogin,
                    picture: null,
                    needRoles: 'ROLE_USER',
                },
                {
                    name: '模型打包',
                    url: '/packaging',
                    picture: null,
                    needRoles: '',
                },
                {
                    name: '模型导入',
                    url: '/onboarding',
                    picture: null,
                    needRoles: 'ROLE_USER',
                },
                {
                    name: '我的关注',
                    url: '/ppersonal/#/star/' + this.userLogin,
                    picture: null,
                    needRoles: 'ROLE_USER',
                },
                {
                    name: '我的任务',
                    url: '/ppersonal/#/task',
                    picture: null,
                    needRoles: 'ROLE_USER',
                },
            ];

            this.apps.forEach((app) => {
                this.uaaClient.get_random_avatar({
                    size: 300,
                }).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            app.picture = res.body['value'];
                        }
                    }
                );
            });
        });
    }

    hasAnyRole(roles: string) {
        if (!roles) {
            return true;
        }
        return this.principal.hasAnyAuthority(roles.split(','));
    }

    gotoUrl(url: string) {
        if (url.includes('#')) {
            window.location.href = url;
        } else {
            this.router.navigate([url]);
        }
    }

}
