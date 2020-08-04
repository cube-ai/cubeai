import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Application} from '../../shared/model/application.model';
import {ApplicationService, Principal, RandomPictureService} from '../../shared';

@Component({
    selector: 'jhi-app-list',
    templateUrl: './app-list.component.html',
})
export class AppListComponent implements OnInit {
    applications: Application[] = [];
    predicate = 'displayOrder';
    orderAsc = false;
    idAsc = true;

    constructor(private router: Router,
                private applicationService: ApplicationService,
                private randomPictureService: RandomPictureService,
                private principal: Principal,
                ) {
    }

    ngOnInit() {
    }

    loadAll(navTitle: string) {
        this.principal.updateCurrentAccount().then(() => {
            this.applicationService.queryAppWithPictures({
                subject1: navTitle,
                sort: this.sort(),
            }).subscribe(
                (res) => {
                    this.applications = res.body;
                    this.applications.forEach((application) => {
                        if (!application.pictureUrl) {
                            this.randomPictureService.getRandomPicture(300, 300).subscribe(
                                (res1) => {
                                    application.pictureUrl = res1.body.pictureDataUrl;
                                }
                            );
                        }
                    });
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
