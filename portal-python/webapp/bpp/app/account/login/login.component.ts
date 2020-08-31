import {Component, OnInit, ViewChild, ElementRef, OnDestroy} from '@angular/core';
import { Location } from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {Principal, HttpService} from '../../shared';

@Component({
    templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit, OnDestroy {
    reason: string;
    redirectUrl: string;
    authenticationError: boolean;
    errorCode: string;
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
        private http: HttpService,
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
        this.http.login({
            username: this.username,
            password: this.password,
            rememberMe: this.rememberMe,
            verifyId: this.verifyId,
            verifyCode: this.myCode,
        }).subscribe((res) => {
            if (res.body['status'] === 'ok') {
                this.authenticationError = false;
                this.updateCurrentAccountAndNavigate();
            } else {
                this.errorCode = res.body['value'];
                this.authenticationError = true;
            }
        }, (err) => {
            this.errorCode = err.error;
            this.authenticationError = true;
        });
    }

    updateCurrentAccountAndNavigate() {
        this.principal.updateCurrentAccount().then((currentAccount) => {
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
        const body = {
            action: 'get_verify_code',
            args: {},
        };
        this.http.post('uaa', body).subscribe(
            (res) => {
                const result = res.body;
                if (result['status'] === 'ok') {
                    this.verifyId = result['value']['verifyId'];
                    this.verifyPicture = result['value']['verifyCode'];
                }
            }
        );
    }

    goBack() {
        this.location.back();
    }

}
