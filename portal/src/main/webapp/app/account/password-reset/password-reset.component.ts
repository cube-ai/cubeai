import {Component, OnInit, ViewChild, ElementRef, OnDestroy} from '@angular/core';
import { Location } from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {FormControl, Validators} from '@angular/forms';
import {NoEmailValidator, passwordMatchValidator, passwordStrongValidator} from '../../shared/form-validators';
import {PasswordResetInitService} from '../service/password-reset-init.service';
import {PasswordResetFinishService} from '../service/password-reset-finish.service';
import {GlobalService, SnackBarService, VerifyCodeService} from '../../shared';

@Component({
    selector: 'jhi-password-reset',
    templateUrl: './password-reset.component.html'
})
export class PasswordResetComponent implements OnInit, OnDestroy {
    email: FormControl;
    password: FormControl;
    confirmPassword: FormControl;
    myCode: FormControl;

    @ViewChild('verifyCanvas') verifyCanvas: ElementRef;
    verifyId: number;
    verifyPicture: string;
    verifyCodeTimer: any;

    status = 'begin';
    resetKey: string;

    constructor(
        private location: Location,
        private router: Router,
        private route: ActivatedRoute,
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

        this.password = new FormControl('');
        this.password.setValidators([passwordStrongValidator()]);

        this.confirmPassword = new FormControl('');
        this.confirmPassword.setValidators([passwordMatchValidator(this.password)]);

        this.myCode = new FormControl('', {
            validators: [Validators.required],
        });
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
            this.snackBarService.error('重置密码过于频繁！请等待30秒以后再试...');
            return;
        }

        this.passwordResetInitService.save({
            email: this.email.value,
            verifyId: this.verifyId,
            verifyCode: this.myCode.value,
            resetUrlPrefix: location.protocol + '//' + location.host + '/#/passwordreset/'
        }).subscribe(() => {
            this.status = 'find_success';
            this.globalService.lastResetPasswordTime = new Date();
        }, () => {
            this.status = 'find_fail';
        });
    }

    resetPassword() {
        this.passwordResetFinishService.save({
            key: this.resetKey,
            newPassword: this.password.value,
        }).subscribe(() => {
            this.status = 'reset_success';
        }, () => {
            this.status = 'reset_fail';
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

    goLogin() {
        this.router.navigate(['/login']);
    }

    goBack() {
        this.location.back();
    }

}
