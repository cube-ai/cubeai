import { Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UaaClient} from '../../shared';

@Component({
    templateUrl: './activate.component.html'
})
export class ActivateComponent implements OnInit {
    msgs = [];
    status = '';
    activateKey: string;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private uaaClient: UaaClient,
    ) {
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.activateKey = params['activateKey'];
            this.activate();
        });
    }

    activate() {
        this.uaaClient.activate_user({
            key: this.activateKey,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.status = 'success';
                    this.msgs = [{
                        severity: 'success',
                        summary:'帐号激活成功',
                        detail: '可使用注册时输入的用户名和密码进行登录...',
                    }];
                } else {
                    this.status = 'fail';
                    this.msgs = [{
                        severity: 'error',
                        summary:'帐号激活失败',
                        detail: '请检查激活码是否有效，或者是否已经被激活...',
                    }];
                }
            }, () => {
                this.status = 'fail';
                this.msgs = [{
                    severity: 'error',
                    summary:'帐号激活失败',
                    detail: '请检查网络连接是否正常...',
                }];
            });
    }

    goLogin() {
        window.localStorage.setItem('loginRedirectUrl', '/');
        window.localStorage.setItem('loginReason', '');
        this.router.navigate(['/login']);
    }

}
