import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import {SnackBarService} from '../../shared/service/snackbar.service';
import {Principal, AccountService, User} from '..';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UniqueEmailValidator, UniquePhoneValidator} from '../../shared/form-validators';

@Component({
    selector: 'jhi-settings',
    templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {
    formGroup: FormGroup;
    user: User;

    get login() { return this.formGroup.get('login'); }
    get fullName() { return this.formGroup.get('fullName'); }
    get email() { return this.formGroup.get('email'); }
    get phone() { return this.formGroup.get('phone'); }
    get address() { return this.formGroup.get('address'); }

    constructor(
        private formBuilder: FormBuilder,
        public dialogRef: MatDialogRef<SettingsComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private snackBarService: SnackBarService,
        private uniqueEmailValidator: UniqueEmailValidator,
        private uniquePhoneValidator: UniquePhoneValidator,
        private accountService: AccountService,
        private principal: Principal
    ) {
        this.formGroup = this.formBuilder.group({
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
    }

    onClose(): void {
        this.dialogRef.close();
    }

    ngOnInit() {
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

        this.accountService.save(this.user).subscribe(() => {
            this.principal.updateCurrentAccount().then();
            this.snackBarService.success('帐号信息更新成功！');
            this.onClose();
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
}
