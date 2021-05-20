import {Component, OnInit, ViewChild, ElementRef, OnDestroy} from '@angular/core';
import { Location } from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {GlobalService, UaaClient} from '../../shared';

@Component({
    selector: 'my-password-reset',
    templateUrl: './password-reset.component.html'
})
export class PasswordResetComponent implements OnInit, OnDestroy {
    msgs = [];
    email = '';
    password = '';
    confirmPassword = '';
    myCode = '';

    errorEmail = '';
    errorPassword = '';
    errorPasswordConfirm = '';

    @ViewChild('verifyCanvas') verifyCanvas: ElementRef;
    verifyId: number;
    verifyPicture: string;
    verifyCodeTimer: any;

    resetKey: string;
    resetSuccess = false;

    constructor(
        private location: Location,
        private router: Router,
        private route: ActivatedRoute,
        private globalService: GlobalService,
        private uaaClient: UaaClient,
    ) {
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.resetKey = params['resetKey'];
        });

        this.getVerifyCode();
        this.verifyCodeTimer = setInterval(() => {
            this.getVerifyCode();
        },  60 * 1000);
    }

    ngOnDestroy() {
        clearInterval(this.verifyCodeTimer);
    }

    findPassword() {
        const now = new Date();
        const last = this.globalService.lastResetPasswordTime;
        if (last && (now.getTime() - last.getTime() < 30000)) {
            this.msgs = [{
                severity: 'warn',
                detail: '重置密码过于频繁！请等待30秒以后再试...',
            }];
            return;
        }
        this.uaaClient.password_reset_init({
            email: this.email,
            verifyId: this.verifyId,
            verifyCode: this.myCode,
            resetUrlPrefix: location.protocol + '//' + location.host + '/#/passwordreset/'
        }).subscribe((res) => {
            if (res.body['status'] === 'ok') {
                this.globalService.lastResetPasswordTime = new Date();
                this.msgs = [{
                    severity: 'success',
                    detail: '密码重置密钥已发送至你提供的电子邮箱。请点击邮件中链接地址或将其复制到浏览器地址框打开页面，来重置你的新密码...',
                }];
            } else {
                this.msgs = [{
                    severity: 'error',
                    detail: '发送找回密码邮件失败，请检查验证码是否正确...',
                }];
            }
        }, () => {
            this.msgs = [{
                severity: 'error',
                detail: '发送找回密码邮件失败，请检查网络连接...',
            }];
        });
    }

    resetPassword() {
        this.uaaClient.password_reset_finish({
            key: this.resetKey,
            newPassword: this.password,
        }).subscribe((res) => {
            if (res.body['status'] === 'ok') {
                this.resetSuccess = true;
                this.msgs = [{
                    severity: 'success',
                    summary:'密码重置成功',
                    detail: '你可以使用新密码登录...',
                }];
            } else {
                this.resetSuccess = false;
                this.msgs = [{
                    severity: 'error',
                    summary:'密码重置失败',
                    detail: '请检查重置密钥是否有效...',
                }];
            }
        }, () => {
            this.resetSuccess = false;
            this.msgs = [{
                severity: 'error',
                summary:'密码重置失败',
                detail: '网络或服务器故障！',
            }];
        });
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

    goLogin() {
        window.localStorage.setItem('loginRedirectUrl', '/');
        window.localStorage.setItem('loginReason', '');
        this.router.navigate(['/login']);
    }

    goBack() {
        this.location.back();
    }

    checkEmail() {
        this.errorEmail = '';

        if (!this.email) {
            this.errorEmail = '请输入Email地址！';
            return;
        }

        this.errorEmail = '正在验证Email有效性......';
        this.uaaClient.get_email_exist({
            email: this.email,
        }).subscribe((res) => {
            if (res.body['status'] === 'ok' && res.body['value'] !== 1) {
                this.errorEmail = '该Email地址不存在，请另外选择一个！';
            } else {
                this.errorEmail = '';
            }
        }, () => {
            this.errorEmail = '';
        });
    }

    checkPassword() {
        this.errorPassword = '';
        const password = this.password;
        const strong = (password.length > 7 && /[a-z]+/.test(password) && /[A-Z]+/.test(password) && /[0-9]+/.test(password));
        if (!strong) {
            this.errorPassword = '密码应至少8位，且包含大小写字母和数字！';
        }
    }

    checkPasswordConfirm() {
        this.errorPasswordConfirm = '';
        if (this.confirmPassword !== this.password) {
            this.errorPasswordConfirm = '两次输入密码不匹配！';
        }
    }

}
