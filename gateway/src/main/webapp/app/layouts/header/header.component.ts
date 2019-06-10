import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material';
import { ProfileService } from '../profiles/profile.service';
import {SnackBarService, GlobalService} from '../../shared';
import {Principal, LoginService} from '../../account';
import {SettingsComponent, PasswordComponent, PasswordResetComponent, RegisterComponent, LoginComponent} from '../../account';
import {MessageService} from '../../message';

@Component({
    selector: 'jhi-header',
    templateUrl: './header.component.html',
    styleUrls: [
        'header.css'
    ]
})
export class HeaderComponent implements OnInit {
    inProduction: boolean;
    newMsgCount = 0;

    constructor(
        private loginService: LoginService,
        private principal: Principal,
        private profileService: ProfileService,
        private globalService: GlobalService,
        private snackBarService: SnackBarService,
        private messageService: MessageService,
        private router: Router,
        public dialog: MatDialog,
    ) {
    }

    ngOnInit() {
        this.profileService.getProfileInfo().then((profileInfo) => {
            this.inProduction = profileInfo.inProduction;
        });
        this.principal.updateCurrentAccount().then();
        this.globalService.setHeader(this);
        setInterval(() => {
            this.getNewMsgCount();
        }, 120 * 1000);
    }

    getNewMsgCount() {
        if (this.getCurrentAccount() && this.getCurrentAccount().login) {
            this.messageService.getUnreadCount({
                receiver: this.getCurrentAccount().login,
                deleted: false,
            }).subscribe(
                (res) => {
                    this.newMsgCount = res.body;
                }
            );
        }
    }

    toggleSideNav() {
        this.globalService.toggleSideNav();
    }

    getCurrentAccount() {
        return this.principal.getCurrentAccount();
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    hasAuthority(authority: string) {
        return this.principal.hasAuthority(authority);
    }

    logout() {
        this.loginService.logout().then(() => {
            this.router.navigate(['/']);
        });
    }

    login() {
        const dialogRef = this.dialog.open(LoginComponent, {
            width: '600px',
            data: { }
        });
    }

    userSettings() {
        const dialogRef = this.dialog.open(SettingsComponent, {
            width: '600px',
            data: {},
        });
    }

    changePassword() {
        const dialogRef = this.dialog.open(PasswordComponent, {
            width: '600px',
            data: {},
        });
    }

    requestResetPassword() {
        const dialogRef = this.dialog.open(PasswordResetComponent, {
            width: '800px',
            data: {}
        });
    }

    registerUser() {
        const dialogRef = this.dialog.open(RegisterComponent, {
            width: '800px',
            data: {
                activate: false,
            }
        });
    }

    activateUser() {
        const dialogRef = this.dialog.open(RegisterComponent, {
            width: '800px',
            data: {
                activate: true,
            }
        });
    }

    navigateToHelp() {
        this.router.navigate(['/article/cubeai-help']);
    }

    navigateToMessage() {
        this.router.navigate(['/message/msg-inbox']);
    }

    navigateToAdmin() {
        this.router.navigate(['/admin/user-management']);
    }

    navigateToUcumos() {
        this.router.navigate(['/ucumos/market']);
    }

    navigateToAiAbility() {
        this.router.navigate(['/ai-ability/open-ability']);
    }

}
