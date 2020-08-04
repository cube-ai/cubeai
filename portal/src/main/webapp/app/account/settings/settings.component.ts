import { Component, OnInit, } from '@angular/core';
import { Location } from '@angular/common';
import {SnackBarService, Principal, AccountService, User} from '../../shared';
import {PasswordService} from '..';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UniqueEmailValidator, UniquePhoneValidator, passwordMatchValidator, passwordStrongValidator} from '../../shared/form-validators';

@Component({
    selector: 'jhi-settings',
    templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {
    formGroup1: FormGroup;
    formGroup2: FormGroup;
    user: User;

    get login() { return this.formGroup1.get('login'); }
    get fullName() { return this.formGroup1.get('fullName'); }
    get email() { return this.formGroup1.get('email'); }
    get phone() { return this.formGroup1.get('phone'); }
    get address() { return this.formGroup1.get('address'); }
    get password() { return this.formGroup2.get('password'); }
    get confirmPassword() { return this.formGroup2.get('confirmPassword'); }

    constructor(
        private location: Location,
        private formBuilder: FormBuilder,
        private snackBarService: SnackBarService,
        private passwordService: PasswordService,
        private uniqueEmailValidator: UniqueEmailValidator,
        private uniquePhoneValidator: UniquePhoneValidator,
        private accountService: AccountService,
        private principal: Principal
    ) {
        this.formGroup1 = this.formBuilder.group({
            login: [''],
            fullName: ['', [Validators.maxLength(50)]],
            email: ['', {
                validators: [Validators.required, Validators.email, Validators.maxLength(50),
                    Validators.minLength(5)],
                updateOn: 'blur',
            }],
            phone: ['', {
                validators: [Validators.required, Validators.minLength(5), Validators.maxLength(20),
                    Validators.pattern('^[0-9]*$')],
                updateOn: 'blur',
            }],
        });
        this.formGroup2 = this.formBuilder.group({
            password: [''],
            confirmPassword: [''],
        });
        this.password.setValidators([passwordStrongValidator()]);
        this.confirmPassword.setValidators([passwordMatchValidator(this.password)]);
    }

    ngOnInit() {
        this.refreshAccount();
    }

    refreshAccount() {
        this.user = this.copyAccount(this.principal.getCurrentAccount());
        this.login.setValue(this.user.login);
        this.login.disable();
        this.fullName.setValue(this.user.fullName);
        this.email.setValue(this.user.email);
        this.phone.setValue(this.user.phone);
        this.email.setAsyncValidators([this.uniqueEmailValidator.validate(this.user.email)]);
        this.phone.setAsyncValidators([this.uniquePhoneValidator.validate(this.user.phone)]);
    }

    onSubmit() {
        this.user.fullName = this.fullName.value;
        this.user.phone = this.phone.value;
        this.user.email = this.email.value;
        this.email.setAsyncValidators([]);
        this.phone.setAsyncValidators([]);

        this.accountService.save(this.user).subscribe(() => {
            this.principal.updateCurrentAccount().then(
                () => {
                    this.refreshAccount();
                    this.snackBarService.success('帐号信息更新成功！');
                }
            );
        }, () => {
            this.snackBarService.error('帐号信息更新失败！');
        });
    }

    copyAccount(account) {
        return {
            activated: account.activated,
            email: account.email,
            fullName: account.fullName,
            langKey: account.langKey,
            phone: account.phone,
            login: account.login,
            imageUrl: account.imageUrl
        };
    }

    onChangePassword() {
        this.passwordService.save(this.password.value).subscribe(() => {
            this.snackBarService.success('修改密码成功！');
        }, () => {
            this.snackBarService.error('修改密码失败！');
        });
    }

    goBack() {
        this.location.back();
    }
}
