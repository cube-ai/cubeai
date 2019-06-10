import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import {SnackBarService} from '../../shared/service/snackbar.service';
import {Principal, User} from '..';
import { PasswordService } from './password.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {passwordMatchValidator, passwordStrongValidator} from '../../shared/form-validators';

@Component({
    selector: 'jhi-password',
    templateUrl: './password.component.html'
})
export class PasswordComponent implements OnInit {
    formGroup: FormGroup;

    get login() { return this.formGroup.get('login'); }
    get password() { return this.formGroup.get('password'); }
    get confirmPassword() { return this.formGroup.get('confirmPassword'); }

    constructor(
        private formBuilder: FormBuilder,
        public dialogRef: MatDialogRef<PasswordComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private passwordService: PasswordService,
        private snackBarService: SnackBarService,
        private principal: Principal
    ) {
        this.formGroup = this.formBuilder.group({
            login: [''],
            password: [''],
            confirmPassword: [''],
        });

        this.password.setValidators([passwordStrongValidator()]);
        this.confirmPassword.setValidators([passwordMatchValidator(this.password)]);
    }

    onClose(): void {
        this.dialogRef.close();
    }

    ngOnInit() {
        this.login.setValue(this.principal.getCurrentAccount().login);
        this.login.disable();
    }

    onSubmit() {
        this.passwordService.save(this.password.value).subscribe(() => {
                this.snackBarService.success('修改密码成功！');
                this.onClose();
            }, () => {
                this.snackBarService.error('修改密码失败！');
            });
    }

}
