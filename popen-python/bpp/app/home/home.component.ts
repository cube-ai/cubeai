import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Principal} from '../shared';
import { DomSanitizer } from '@angular/platform-browser';
import {RandomPictureService} from '../shared';

@Component({
    templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {

    userLogin: string;
    apps = [];

    constructor(
        private router: Router,
        private principal: Principal,
        private sanitizer: DomSanitizer,
        private randomPictureService: RandomPictureService,
    ) {}

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.apps = [
                {
                    name: '所有能力',
                    url: '/market',
                    picture: null,
                    needRoles: '',
                },
                {
                    name: '我的能力',
                    url: '/personal/' + this.userLogin,
                    picture: null,
                    needRoles: 'ROLE_USER',
                },
                {
                    name: '应用示范',
                    url: '/demo',
                    picture: null,
                    needRoles: '',
                },
            ];

            this.apps.forEach((app) => {
                this.randomPictureService.getRandomPicture(30, 30).subscribe(
                    (res) => {
                        app.picture = this.sanitizer.bypassSecurityTrustStyle(`url(${res.body.pictureDataUrl})`);
                    }
                );
            });
        });
    }

    hasAnyRole(roles: string) {
        if (!roles) {
            return true;
        }
        return this.principal.hasAnyAuthority(roles.split(','));
    }

    gotoUrl(url: string) {
        if (url.includes('#')) {
            window.location.href = url;
        } else {
            this.router.navigate([url]);
        }
    }

}
