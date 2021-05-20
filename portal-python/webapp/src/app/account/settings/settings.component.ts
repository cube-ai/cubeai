import { Component, OnInit, } from '@angular/core';
import { Location } from '@angular/common';
import { Principal, UaaClient, User} from '../../shared';
import {MessageService} from 'primeng/api';
import {FileUploader} from 'ng2-file-upload';
import {FileItem} from 'ng2-file-upload/file-upload/file-item.class';
import {ImageCroppedEvent} from "ngx-image-cropper";

@Component({
    templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {
    user: User;
    oldUser: User;
    confirmPassword = '';

    errorEmail = '';
    errorPhone = '';
    errorPassword = '';
    errorPasswordConfirm = '';
    patternLogin = new RegExp('^[_.@A-Za-z0-9]*$');
    patternEmail = new RegExp('^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$');
    patternPhone = new RegExp('^[0-9]*$');

    imgSelector: FileUploader;
    inputPictureUrl: string;

    constructor(
        private location: Location,
        private messageService: MessageService,
        private uaaClient: UaaClient,
        private principal: Principal
    ) {
    }

    ngOnInit() {
        this.loadUser();

        this.imgSelector = new FileUploader({});
        this.imgSelector.onAfterAddingFile = (fileItem: FileItem) => {
            if (!fileItem.file.type.startsWith('image')) {
                this.messageService.add({severity:'error', detail:'文件类型必须是图片！'});
                fileItem.remove();
                return;
            }

            if (this.imgSelector.queue.length > 1) {
                this.imgSelector.queue[0].remove();
            }

            this.readImgFile(fileItem._file);
        };
    }

    readImgFile(file: File) {
        const fileReader = new FileReader();
        fileReader.readAsDataURL(file);
        fileReader.onload = () => {
            this.inputPictureUrl = fileReader.result as string;
        };
    }

    saveCroppedImage(event: ImageCroppedEvent) {
        this.user.imageUrl = event.base64;
    }

    loadUser() {
        this.principal.updateCurrentAccount().then(
            () => {
                this.user = this.principal.getCurrentAccount();
                this.user.password = '';

                if (!this.user.imageUrl || !(this.user.imageUrl.startsWith('data') || this.user.imageUrl.startsWith('http'))) {
                    this.uaaClient.get_random_avatar({
                        size: 200,
                    }).subscribe(
                        (res) => {
                            if (res.body['status'] === 'ok') {
                                this.user.imageUrl = res.body['value'];
                            }
                        }
                    );
                }

                this.oldUser = this.copyUser(this.user);
            }
        );


    }

    saveUser() {
        this.uaaClient.update_current_account({
            user: this.user,
        }).subscribe(() => {
            this.principal.updateCurrentAccount().then(
                () => {
                    this.user = this.principal.getCurrentAccount();
                    this.user.password = '';
                    this.oldUser = this.copyUser(this.user);
                    this.messageService.add({severity:'success', detail:'帐号信息更新成功！'});
                }
            );
        }, () => {
            this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
        });
    }

    copyUser(user) {
        return {
            activated: user.activated,
            email: user.email,
            fullName: user.fullName,
            langKey: user.langKey,
            phone: user.phone,
            login: user.login,
            authorities: user.authorities,
            imageUrl: user.imageUrl
        };
    }

    onChangePassword() {
        this.uaaClient.change_password({
            password: this.user.password,
        }).subscribe((res) => {
            if (res.body['status'] === 'ok') {
                this.messageService.add({severity:'success', detail:'修改密码成功！'});
            } else {
                this.messageService.add({severity:'error', detail:'修改密码失败！'});
            }
        }, () => {
            this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
        });
    }

    checkEmail() {
        this.errorEmail = '';

        if (this.user.email === this.oldUser.email) {
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

        if (this.user.phone === this.oldUser.phone) {
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

    checkPasswordConfirm() {
        this.errorPasswordConfirm = '';
        if (this.confirmPassword !== this.user.password) {
            this.errorPasswordConfirm = '两次输入密码不匹配！';
        }
    }

    goBack() {
        this.location.back();
    }
}
