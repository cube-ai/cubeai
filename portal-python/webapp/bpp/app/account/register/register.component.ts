import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import {User, UaaClient} from '../../shared';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UniqueEmailValidator, UniqueLoginValidator, UniquePhoneValidator, passwordMatchValidator, passwordStrongValidator} from '../../shared/form-validators';

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

    constructor(
        private location: Location,
        private formBuilder: FormBuilder,
        private uaaClient: UaaClient,
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
    }

    onRegister() {
        this.user.login = this.login.value;
        this.user.fullName = this.fullName.value;
        this.user.phone = this.phone.value;
        this.user.email = this.email.value;
        this.user.password = this.password.value;
        this.user.langKey = 'en';
        this.user.activateUrlPrefix = location.protocol + '//' + location.host + '/#/activate/';

        this.uaaClient.register_user({
            user: this.user,
        }).subscribe((res) => {
            if (res.body['status'] === 'ok') {
                this.status = 'success';
            } else {
                this.status = 'fail';
            }
        }, () => {
            this.status = 'fail';
        });
    }

    goBack() {
        this.location.back();
    }

}
