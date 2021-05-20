import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { Subscription } from 'rxjs';
import { Location } from '@angular/common';
import {Principal, User, UaaClient, GlobalService} from '../../shared';
import {MessageService} from 'primeng/api';

@Component({
    templateUrl: './user-detail.component.html'
})
export class UserDetailComponent implements OnInit {
    subscription: Subscription;
    login: string;
    user: User;
    authorities = [];
    mode: string;
    headline: string;

    selfEmail: string;
    selfPhone: string;

    errorLogin = '';
    errorEmail = '';
    errorPhone = '';
    errorPassword = '';
    errorPasswordConfirm = '';
    patternLogin = new RegExp('^[_.@A-Za-z0-9]*$');
    patternEmail = new RegExp('^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$');
    patternPhone = new RegExp('^[0-9]*$');
    showAuthorities = true;

    constructor(private principal: Principal,
                private location: Location,
                private route: ActivatedRoute,
                private router: Router,
                public globalService: GlobalService,
                private uaaClient: UaaClient,
                private messageService: MessageService,
    ) {
    }

    goBack() {
        this.location.back();
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.mode = params['mode'];
            this.login = params['login'];

            if (this.mode === 'create') {
                this.user = new User();
                this.user.activated = true;
                this.user.authorities = ['ROLE_USER'];
            } else {
                this.uaaClient.find_user({
                    login: this.login,
                }).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.user = res.body['value'];
                            this.selfEmail = this.user.email;
                            this.selfPhone = this.user.phone;
                        } else {
                            this.messageService.add({severity:'error', detail:'获取用户信息出错！'});
                            this.goBack();
                        }
                    }, () => {
                        this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
                        this.goBack();
                    }
                );
            }

            switch (this.mode) {
                case 'create':
                    this.headline = '新建用户';
                    break;
                case 'edit':
                    this.headline = '编辑用户';
                    break;
                case 'view':
                    this.headline = '查看用户';
                    break;
                case 'password':
                    this.headline = '修改密码';
                    break;
            }

            this.uaaClient.get_authorities({}).subscribe(
                (res1) => {
                    if (res1.body['status'] === 'ok') {
                        this.authorities = res1.body['value'];
                    }
                }
            );
        });

    }

    createUser() {
        this.uaaClient.create_user({
            user: this.user,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.messageService.add({severity:'success', detail:'新用户创建成功！'});
                    this.goBack();
                } else {
                    this.messageService.add({severity:'error', detail:'创建新用户失败！'});
                }
            }, () => {
                this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
            }
        );
    }

    updateUser() {
        this.uaaClient.update_user({
            user: this.user,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.principal.updateCurrentAccount().then();
                    this.messageService.add({severity:'success', detail:'用户信息更新成功！'});
                    this.goBack();
                } else {
                    this.messageService.add({severity:'error', detail:'用户信息更新失败！'});
                }
            }, () => {
                this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
            }
        );
    }

    resetPassword() {
        this.uaaClient.update_user({
            user: this.user,
        }).subscribe((res) => {
            if (res.body['status'] === 'ok') {
                this.messageService.add({severity:'success', detail:'用户密码修改成功！'});
                this.goBack();
            } else {
                this.messageService.add({severity:'error', detail:'修改用户密码失败！'});
            }
        }, () => {
            this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
        });
    }

    checkLogin() {
        this.errorLogin = '';
        if (this.user.login.length < 1) {
            this.errorLogin = '用户名必填！';
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

        if (this.mode === 'edit' && this.user.email === this.selfEmail) {
            return;
        }

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

        if (this.mode === 'edit' && this.user.phone === this.selfPhone) {
            return;
        }

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

    checkAuthorities() {
        if (!this.user.authorities.includes('ROLE_USER')){
            this.user.authorities.push('ROLE_USER');
            setTimeout(() => this.showAuthorities = false);
            setTimeout(() => this.showAuthorities = true);
        }
    }

    shouldDisableCreate(): boolean {
        return !this.user.login || !this.user.email || !this.user.phone || !this.user.password
            || !!this.errorLogin || !!this.errorEmail || !!this.errorPhone || !!this.errorPassword;

    }

    shouldDisableUpdate(): boolean {
        return !!this.errorEmail || !!this.errorPhone;
    }

}
