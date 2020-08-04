import {Component, OnInit, ViewChild, ElementRef, OnDestroy} from '@angular/core';
import { Location } from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {User, Principal, LoginService, VerifyCodeService} from '../../shared';

@Component({
    templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit, OnDestroy {
    reason: string;
    redirectUrl: string;
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
        private location: Location,
        private router: Router,
        private route: ActivatedRoute,
        private principal: Principal,
        private loginService: LoginService,
        private verifyCodeService: VerifyCodeService,
    ) {
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.reason = params['reason'];
            this.redirectUrl = params['redirectUrl'];
            if (!this.redirectUrl) {
                this.redirectUrl = '/';
            }
            this.redirectUrl = this.redirectUrl.replace(/\$/g, '/');
            this.redirectUrl = this.redirectUrl.replace(/@/g, '#');
        });

        this.getVerifyCode();
        this.verifyCodeTimer = setInterval(() => {
            this.getVerifyCode();
        },  60 * 1000);
    }

    ngOnDestroy() {
        clearInterval(this.verifyCodeTimer);
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
            this.updateCurrentAccountAndNavigate();
        }).catch(() => {
            this.authenticationError = true;
        });
    }

    updateCurrentAccountAndNavigate() {
        this.principal.updateCurrentAccount().then((currentAccount) => {
            this.currentAccount = currentAccount;
            if (currentAccount) {
                this.navigate();
            }
        });
    }

    navigate() {
        if (this.redirectUrl) {
            // this.router.navigate([this.redirectUrl]);
            window.location.href = this.redirectUrl;
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

    goBack() {
        this.location.back();
    }

}
