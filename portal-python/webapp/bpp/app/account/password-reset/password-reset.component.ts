import {Component, OnInit, ViewChild, ElementRef, OnDestroy} from '@angular/core';
import { Location } from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {FormControl, Validators} from '@angular/forms';
import {NoEmailValidator, passwordMatchValidator, passwordStrongValidator} from '../../shared/form-validators';
import {GlobalService, SnackBarService, UaaClient} from '../../shared';

@Component({
    selector: 'my-password-reset',
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
        private noEmailValidator: NoEmailValidator,
        private snackBarService: SnackBarService,
        private globalService: GlobalService,
        private uaaClient: UaaClient,
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
        this.uaaClient.password_reset_init({
            email: this.email.value,
            verifyId: this.verifyId,
            verifyCode: this.myCode.value,
            resetUrlPrefix: location.protocol + '//' + location.host + '/#/passwordreset/'
        }).subscribe((res) => {
            if (res.body['status'] === 'ok') {
                this.status = 'find_success';
                this.globalService.lastResetPasswordTime = new Date();
            } else {
                this.status = 'find_fail';
            }
        }, () => {
            this.status = 'find_fail';
        });
    }

    resetPassword() {
        this.uaaClient.password_reset_finish({
            key: this.resetKey,
            newPassword: this.password.value,
        }).subscribe((res) => {
            if (res.body['status'] === 'ok') {
                this.status = 'reset_success';
            } else {
                this.status = 'reset_fail';
            }
        }, () => {
            this.status = 'reset_fail';
        });
    }

    getVerifyCode() {
        this.uaaClient.get_verify_code({}).subscribe(
            (res) => {
                const result = res.body;
                if (result['status'] === 'ok') {
                    this.verifyId = result['value']['verifyId'];
                    this.verifyPicture = result['value']['verifyCode'];
                }
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
