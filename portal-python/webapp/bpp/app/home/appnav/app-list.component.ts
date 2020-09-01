import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Application} from '../../shared/model/application.model';
import {UaaClient, Principal} from '../../shared';

@Component({
    selector: 'my-app-list',
    templateUrl: './app-list.component.html',
})
export class AppListComponent implements OnInit {
    applications: Application[] = [];
    predicate = 'displayOrder';
    orderAsc = false;
    idAsc = true;

    constructor(private router: Router,
                private uaaClient: UaaClient,
                private principal: Principal,
                ) {
    }

    ngOnInit() {
    }

    loadAll(navTitle: string) {
        this.principal.updateCurrentAccount().then(() => {
            this.uaaClient.get_applications({
                subject1: navTitle,
                sort: this.sort(),
                with_picture: 1,
            }).subscribe(
                (res) => {
                    const result = res.body;
                    if (result['status'] === 'ok') {
                        this.applications = result['value']['results'];
                        this.applications.forEach((application) => {
                            if (!application.pictureUrl) {
                                this.uaaClient.get_random_picture({
                                    width: 300,
                                    height: 300,
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

    sort() {
        const result = [this.predicate + ',' + (this.orderAsc ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id' + ',' + (this.idAsc ? 'asc' : 'desc'));
        }
        return result;
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
