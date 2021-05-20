import {Component, OnInit, OnDestroy} from '@angular/core';
import { Location } from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {Principal, GlobalService, LoginService, UaaClient} from '../../shared';

@Component({
    templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit, OnDestroy {
    reason: string;
    msgs = [];
    redirectUrl: string;
    errorCode: string;
    username: string;
    password: string;
    rememberMe = false;
    verifyId: number;
    verifyPicture: string;
    myCode = '';
    verifyCodeTimer: any;

    constructor(
        private location: Location,
        private router: Router,
        private route: ActivatedRoute,
        private principal: Principal,
        private globalService: GlobalService,
        private loginSevice: LoginService,
        private uaaClient: UaaClient,
    ) {
    }

    ngOnInit() {
        this.redirectUrl = window.localStorage.getItem('loginRedirectUrl');
        if (!this.redirectUrl) {
            this.redirectUrl = '/';
        }
        this.reason = window.localStorage.getItem('loginReason');
        if (this.reason) {
            this.msgs = [{severity: 'warn', detail: this.reason}];
        }
        window.localStorage.setItem('loginRedirectUrl', '/');
        window.localStorage.setItem('loginReason', '');

        this.getVerifyCode();
        this.verifyCodeTimer = setInterval(() => {
            this.getVerifyCode();
        },  60 * 1000);
    }

    ngOnDestroy() {
        clearInterval(this.verifyCodeTimer);
    }

    login() {
        this.loginSevice.login({
            username: this.username,
            password: this.password,
            rememberMe: this.rememberMe,
            verifyId: this.verifyId,
            verifyCode: this.myCode,
        }).subscribe((res) => {
            if (res.body['status'] === 'ok') {
                this.msgs = [];
                this.updateCurrentAccountAndNavigate();
            } else {
                this.errorCode = res.body['value'];
                this.msgs = [{severity: 'error', detail: this.getErrorInfo(this.errorCode)}];
            }
        }, (err) => {
            this.errorCode = err.error;
            this.msgs = [{severity: 'error', detail: this.getErrorInfo(this.errorCode)}];
        });
    }

    updateCurrentAccountAndNavigate() {
        this.principal.updateCurrentAccount().then((currentAccount) => {
            if (currentAccount) {
                this.navigate();
                this.globalService.getHeader().refreshUnreadMsgCount();
            }
        });
    }

    navigate() {
        if (this.redirectUrl) {
            // this.router.navigate([this.redirectUrl]);
            window.location.href = this.redirectUrl;
        }
    }

    getVerifyCode() {
        this.uaaClient.get_verify_code({}).subscribe(
            (res) => {
                const result = res.body;
                if (result['status'] === 'ok') {
                    this.verifyId = result['value']['verifyId'];
                    this.verifyPicture = result['value']['verifyCode'];
                }
            }
        );
    }

    getErrorInfo(code) {
        if (code === 'password_error') {
            return '用户名/密码错误';
        }
        if (code === 'verify_code_error') {
            return '验证码不匹配';
        }
        return '网络或服务器故障';
    }

    goBack() {
        this.location.back();
    }
}
