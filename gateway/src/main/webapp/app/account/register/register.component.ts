import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { RegisterService } from './register.service';
import {SnackBarService} from '../../shared/service/snackbar.service';
import {User} from '..';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UniqueEmailValidator, UniqueLoginValidator, UniquePhoneValidator, passwordMatchValidator, passwordStrongValidator} from '../../shared/form-validators';
import {ActivateService} from '../service/activate.service';

@Component({
    templateUrl: './register.component.html'
})
export class RegisterComponent implements OnInit {
    formGroup: FormGroup;
    user: User;
    status = 'begin';

    get login() { return this.formGroup.get('login'); }
    get fullName() { return this.formGroup.get('fullName'); }
    get email() { return this.formGroup.get('email'); }
    get phone() { return this.formGroup.get('phone'); }
    get password() { return this.formGroup.get('password'); }
    get confirmPassword() { return this.formGroup.get('confirmPassword'); }
    get activateKey() { return this.formGroup.get('activateKey'); }

    constructor(
        private formBuilder: FormBuilder,
        public dialogRef: MatDialogRef<RegisterComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private activateService: ActivateService,
        private registerService: RegisterService,
        private snackBarService: SnackBarService,
        private uniqueLoginValidator: UniqueLoginValidator,
        private uniqueEmailValidator: UniqueEmailValidator,
        private uniquePhoneValidator: UniquePhoneValidator,
    ) {
        this.formGroup = this.formBuilder.group({
            login: ['', {
                validators: [Validators.required, Validators.maxLength(50),
                    Validators.pattern('^[_.@A-Za-z0-9-]*$')],
                asyncValidators: [this.uniqueLoginValidator.validate()],
                updateOn: 'blur',
            }],
            fullName: ['', [Validators.maxLength(50)]],
            email: ['', {
                validators: [Validators.required, Validators.email, Validators.maxLength(50),
                    Validators.minLength(5)],
                asyncValidators: [this.uniqueEmailValidator.validate()],
                updateOn: 'blur',
            }],
            phone: ['', {
                validators: [Validators.required, Validators.minLength(5), Validators.maxLength(20),
                    Validators.pattern('^[0-9]*$')],
                asyncValidators: [this.uniquePhoneValidator.validate()],
                updateOn: 'blur',
            }],
            password: [''],
            confirmPassword: [''],
            activateKey: [''],
        });

        this.password.setValidators([passwordStrongValidator()]);
        this.confirmPassword.setValidators([passwordMatchValidator(this.password)]);
    }

    ngOnInit() {
        this.user = new User();
        if (this.data.activate) {
            this.status = 'success';
        }
    }

    onClose(): void {
        this.dialogRef.close();
    }

    onRegister() {
        this.user.login = this.login.value;
        this.user.fullName = this.fullName.value;
        this.user.phone = this.phone.value;
        this.user.email = this.email.value;
        this.user.password = this.password.value;
        this.user.langKey = 'en';
        this.registerService.save(this.user).subscribe(() => {
            this.status = 'success';
            this.snackBarService.success('用户注册成功！请使用新注册的用户帐号和密码登录...');
        }, () => {
            this.status = 'fail';
            this.snackBarService.error('注册信息发送失败，请稍后再试！');
        });
    }

    onActivate() {
        this.activateService.get(this.activateKey.value).subscribe(
            () => {
                this.status = 'activate_success';
            }, () => {
                this.status = 'activate_fail';
            });
    }

}
