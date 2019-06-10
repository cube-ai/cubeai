import {Component, OnInit, Inject, ViewChild, ElementRef, OnDestroy} from '@angular/core';
import { Router } from '@angular/router';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import {User, Principal} from '..';
import {LoginService} from '../service/login.service';
import {VerifyCodeService} from '../service/verify-code.service';
import {GlobalService} from '../../shared';

@Component({
    templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit, OnDestroy {
    authenticationError: boolean;
    currentAccount: User;
    username: string;
    password: string;
    rememberMe = false;

    @ViewChild('verifyCanvas') verifyCanvas: ElementRef;
    verifyId: number;
    verifyPicture: string;
    myCode = '';
    verifyCodeTimer: any;

    constructor(
        public dialogRef: MatDialogRef<LoginComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private router: Router,
        private principal: Principal,
        private loginService: LoginService,
        private verifyCodeService: VerifyCodeService,
        private globalService: GlobalService,
    ) {
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

    onClose(): void {
        this.dialogRef.close();
    }

    login() {
        this.loginService.login({
            username: this.username,
            password: this.password,
            rememberMe: this.rememberMe,
            verifyId: this.verifyId,
            verifyCode: this.myCode,
        }).then(() => {
            this.authenticationError = false;
            this.dialogRef.close();
            this.updateCurrentAccountAndNavigate();
        }).catch(() => {
            this.authenticationError = true;
        });
    }

    updateCurrentAccountAndNavigate() {
        this.principal.updateCurrentAccount().then((currentAccount) => {
            this.globalService.getHeader().getNewMsgCount();
            this.currentAccount = currentAccount;
            if (currentAccount) {
                this.navigate();
            }
        });
    }

    navigate() {
        if (this.data.redirectUrl) {
            this.router.navigate([this.data.redirectUrl]);
        } else if (this.principal.hasAuthority('ROLE_USER')
            || this.principal.hasAuthority('ROLE_MANAGER')
            || this.principal.hasAuthority('ROLE_DEVELOPER')) {
            this.router.navigate(['/ucumos/market']);
        } else if (this.principal.hasAuthority('ROLE_ADMIN')) {
            this.router.navigate(['/admin/user-management']);
        } else {
            this.router.navigate(['/ucumos/market']);
        }
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
