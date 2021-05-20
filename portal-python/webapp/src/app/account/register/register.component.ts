import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import {User, UaaClient} from '../../shared';

@Component({
    templateUrl: './register.component.html'
})
export class RegisterComponent implements OnInit {
    msgs = [];
    user: User;
    confirmPassword = '';

    errorLogin = '';
    errorEmail = '';
    errorPhone = '';
    errorPassword = '';
    errorPasswordConfirm = '';
    patternLogin = new RegExp('^[_.@A-Za-z0-9]*$');
    patternEmail = new RegExp('^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$');
    patternPhone = new RegExp('^[0-9]*$');

    constructor(
        private location: Location,
        private uaaClient: UaaClient,
    ) {
    }

    ngOnInit() {
        this.user = new User();
    }

    register() {
        this.user.activateUrlPrefix = location.protocol + '//' + location.host + '/#/activate/';

        this.uaaClient.register_user({
            user: this.user,
        }).subscribe((res) => {
            if (res.body['status'] === 'ok') {
                this.msgs = [{
                    severity: 'success',
                    summary:'注册成功',
                    detail: '帐号激活码已发送至你注册时提供的电子邮箱，请点击邮件中链接地址或将其复制到浏览器地址框打开页面来激活帐号...',
                }];
            } else {
                this.msgs = [{
                    severity: 'error',
                    summary:'注册失败',
                    detail: '请检查 用户名|邮箱地址|手机号 是否已经被注册...',
                }];
            }
        }, () => {
            this.msgs = [{
                severity: 'error',
                summary:'注册失败',
                detail: '网络或服务器故障！',
            }];
        });
    }

    checkLogin() {
        this.errorLogin = '';
        if (this.user.login.length < 3) {
            this.errorLogin = '用户名太短！';
            return;
        }

        if (this.user.login.length > 20) {
            this.errorLogin = '用户名长度不得大于20！';
            return;
        }

        if (!this.patternLogin.test(this.user.login)) {
            this.errorLogin = '用户名中含有非法字符！';
            return;
        }

        this.errorLogin = '正在验证唯一性......';
        this.uaaClient.get_login_exist({
            login: this.user.login,
        }).subscribe((res) => {
            if (res.body['status'] === 'ok' && res.body['value'] === 1) {
                this.errorLogin = '该用户名已经被注册，请另外选择一个！';
            } else {
                this.errorLogin = '';
            }
        }, () => {
            this.errorLogin = '';
        });
    }

    checkEmail() {
        this.errorEmail = '';
        if (this.user.email.length < 5) {
            this.errorEmail = 'Email长度不得小于5！';
            return;
        }

        if (this.user.email.length > 50) {
            this.errorEmail = 'Email长度不得大于50！';
            return;
        }

        if (!this.patternEmail.test(this.user.email)) {
            this.errorEmail = 'Email地址无效！';
            return;
        }

        this.errorEmail = '正在验证唯一性......';
        this.uaaClient.get_email_exist({
            email: this.user.email,
        }).subscribe((res) => {
            if (res.body['status'] === 'ok' && res.body['value'] === 1) {
                this.errorEmail = '该Email地址已经被注册，请另外选择一个！';
            } else {
                this.errorEmail = '';
            }
        }, () => {
            this.errorEmail = '';
        });
    }

    checkPhone() {
        this.errorPhone = '';
        if (this.user.phone.length < 5) {
            this.errorPhone = '手机号码长度不得小于5！';
            return;
        }

        if (this.user.phone.length > 20) {
            this.errorPhone = '手机号码长度不得大于20！';
            return;
        }

        if (!this.patternPhone.test(this.user.phone)) {
            this.errorPhone = '手机号码只能包含数字！';
            return;
        }

        this.errorPhone = '正在验证唯一性......';
        this.uaaClient.get_phone_exist({
            phone: this.user.phone,
        }).subscribe((res) => {
            if (res.body['status'] === 'ok' && res.body['value'] === 1) {
                this.errorPhone = '该手机号已经被注册，请另外选择一个！';
            } else {
                this.errorPhone = '';
            }
        }, () => {
            this.errorPhone = '';
        });
    }

    checkPassword() {
        this.errorPassword = '';
        const password = this.user.password;
        const strong = (password.length > 7 && /[a-z]+/.test(password) && /[A-Z]+/.test(password) && /[0-9]+/.test(password));
        if (!strong) {
            this.errorPassword = '密码应至少8位，且包含大小写字母和数字！';
        }
    }

    checkPasswordConfirm() {
        this.errorPasswordConfirm = '';
        if (this.confirmPassword !== this.user.password) {
            this.errorPasswordConfirm = '两次输入密码不匹配！';
        }
    }

    shoudDisableRegister(): boolean {
        return !this.user.login || !this.user.email || !this.user.phone || !this.user.password || !this.confirmPassword
            || !!this.errorLogin || !!this.errorEmail || !!this.errorPhone || !!this.errorPassword || !!this.errorPasswordConfirm;
    }

    goBack() {
        this.location.back();
    }

}
