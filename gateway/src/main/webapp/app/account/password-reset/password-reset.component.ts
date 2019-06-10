import {Component, OnInit, Inject, ViewChild, ElementRef, OnDestroy} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import {FormControl, Validators} from '@angular/forms';
import {NoEmailValidator, passwordMatchValidator, passwordStrongValidator} from '../../shared/form-validators';
import {PasswordResetInitService} from '../service/password-reset-init.service';
import {PasswordResetFinishService} from '../service/password-reset-finish.service';
import {VerifyCodeService} from '../service/verify-code.service';
import {GlobalService, SnackBarService} from '../../shared';

@Component({
    selector: 'jhi-password-reset',
    templateUrl: './password-reset.component.html'
})
export class PasswordResetComponent implements OnInit, OnDestroy {
    email: FormControl;
    resetKey: FormControl;
    password: FormControl;
    confirmPassword: FormControl;
    myCode: FormControl;

    @ViewChild('verifyCanvas') verifyCanvas: ElementRef;
    verifyId: number;
    verifyPicture: string;
    verifyCodeTimer: any;

    status = 'begin';

    constructor(
        public dialogRef: MatDialogRef<PasswordResetComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private passwordResetInitService: PasswordResetInitService,
        private passwordResetFinishService: PasswordResetFinishService,
        private noEmailValidator: NoEmailValidator,
        private snackBarService: SnackBarService,
        private globalService: GlobalService,
        private verifyCodeService: VerifyCodeService,
    ) {
        this.email = new FormControl('', {
            validators: [Validators.required, Validators.email, Validators.maxLength(50),
                Validators.minLength(5)],
            asyncValidators: [this.noEmailValidator.validate()],
            updateOn: 'blur',
        });

        this.resetKey = new FormControl('', {
            validators: [Validators.required, Validators.maxLength(20), Validators.minLength(20)],
        });

        this.password = new FormControl('');
        this.password.setValidators([passwordStrongValidator()]);

        this.confirmPassword = new FormControl('');
        this.confirmPassword.setValidators([passwordMatchValidator(this.password)]);

        this.myCode = new FormControl('', {
            validators: [Validators.required],
        });
    }

    onClose(): void {
        this.dialogRef.close();
    }

    ngOnInit() {
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
            this.snackBarService.error('重置密码邮件已发送！请等待30秒以后再试...');
            this.onClose();
        }

        this.passwordResetInitService.save({
            email: this.email.value,
            verifyId: this.verifyId,
            verifyCode: this.myCode.value,
        }).subscribe(() => {
            this.status = 'find_sucess';
            this.globalService.lastResetPasswordTime = new Date();
        }, () => {
            this.status = 'find_fail';
        });
    }

    resetPassword() {
        this.passwordResetFinishService.save({
            key: this.resetKey.value,
            newPassword: this.password.value,
        }).subscribe(() => {
            this.snackBarService.success('密码重置成功！请用新密码登录...');
            this.dialogRef.close();
        }, () => {
            this.status = 'reset_fail';
            this.snackBarService.error('密码重置失败！请检查重置密钥是否有效...');
        });
    }

    getVerifyCode() {
        this.verifyCodeService.getVerifyCode().subscribe(
            (res) => {
                const codeJson = res.body;
                this.verifyId = codeJson['verifyId'];
                this.verifyPicture = codeJson['verifyCode'];
            }
        );
    }

}
