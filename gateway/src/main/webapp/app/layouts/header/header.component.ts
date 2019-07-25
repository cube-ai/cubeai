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
        const config = {
            width: '600px',
            data: {},
        };
        if (window.screen.height < 800) {
            config['height'] = '600px';
        }
        this.dialog.open(LoginComponent, config);
    }

    userSettings() {
        const config = {
            width: '600px',
            data: {},
        };
        if (window.screen.height < 800) {
            config['height'] = '600px';
        }
        this.dialog.open(SettingsComponent, config);
    }

    changePassword() {
        const config = {
            width: '600px',
            data: {},
        };
        if (window.screen.height < 800) {
            config['height'] = '600px';
        }
       this.dialog.open(PasswordComponent, config);
    }

    requestResetPassword() {
        const config = {
            width: '800px',
            data: {},
        };
        if (window.screen.height < 800) {
            config['height'] = '600px';
        }
        this.dialog.open(PasswordResetComponent, config);
    }

    registerUser() {
        const config = {
            width: '800px',
            data: {
                activate: false,
            },
        };
        if (window.screen.height < 800) {
            config['height'] = '600px';
        }
        this.dialog.open(RegisterComponent, config);
    }

    activateUser() {
        const config = {
            width: '800px',
            data: {
                activate: true,
            },
        };
        if (window.screen.height < 800) {
            config['height'] = '600px';
        }
        this.dialog.open(RegisterComponent, config);
    }

    navigateToHelp() {
        this.router.navigate(['/article/cubeai-help']);
    }

    navigateToAboutMe() {
        this.router.navigate(['/article/cubeai-aboutme']);
    }

    navigateToMessage() {
        this.router.navigate(['/message/msg-inbox']);
    }

    navigateToAdmin() {
        if (this.principal.hasAuthority('ROLE_ADMIN')) {
            this.router.navigate(['/admin/user-management']);
        } else if (this.principal.hasAuthority('ROLE_CONTENT')) {
            this.router.navigate(['/admin/bulletin']);
        }
    }

    navigateToUcumos() {
        this.router.navigate(['/ucumos/market']);
    }

    navigateToAiAbility() {
        this.router.navigate(['/ai-ability/open-ability']);
    }

    navigateToAiTraining() {
        this.router.navigate(['/article/ai-training']);
    }

    navigateToAiSolution() {
        this.router.navigate(['/article/ai-solution']);
    }

    navigateToAiResource() {
        this.router.navigate(['/article/ai-resource']);
    }

    navigateToAiPartner() {
        this.router.navigate(['/article/ai-partner']);
    }

}
