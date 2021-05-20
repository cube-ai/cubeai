import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Principal, UaaClient} from "../../shared";
import {Application} from "../../shared/model/application.model";

@Component({
    templateUrl: './app-navigate.component.html',
})
export class AppNavigateComponent implements OnInit {
    navTitle: string;
    applications: Application[] = [];

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private uaaClient: UaaClient,
        private principal: Principal,
        ) {
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.navTitle = params['navTitle'];
            this.loadAll(this.navTitle);
        });
    }

    loadAll(navTitle: string) {
        this.principal.updateCurrentAccount().then(() => {
            this.uaaClient.get_applications({
                subject1: navTitle,
                sort: ["displayOrder,desc", "id,asc"],
                with_picture: 1,
            }).subscribe(
                (res) => {
                    const result = res.body;
                    if (result['status'] === 'ok') {
                        this.applications = result['value']['results'];
                        this.applications.forEach((application) => {
                            if (!application.pictureUrl) {
                                this.uaaClient.get_random_avatar({
                                    size: 300,
                                }).subscribe(
                                    (res1) => {
                                        if (res1.body['status'] === 'ok') {
                                            application.pictureUrl = res1.body['value'];
                                        }
                                    }
                                );
                            }
                        });
                    }
                }
            );
        });
    }

    hasAnyRole(roles: string) {
        if (!roles) {
            return true;
        }
        return this.principal.hasAnyAuthority(roles.split(','));
    }

    gotoUrl(url: string) {
        if (url.includes('#') || url.startsWith('http')) {
            window.location.href = url;
        } else {
            this.router.navigate([url]);
        }
    }

}
