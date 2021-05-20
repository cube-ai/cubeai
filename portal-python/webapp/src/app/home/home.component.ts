import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Principal, UaaClient} from '../shared';
import {Application} from '../shared/model/application.model';

@Component({
    templateUrl: './home.component.html',
    styleUrls: [
        'home.css',
    ]
})
export class HomeComponent implements OnInit {
    homepageIntro = '';
    homepagePartners = '';
    applications: Application[] = [];
    responsiveOptions = [
        {
            breakpoint: '2800px',
            numVisible: 7,
            numScroll: 7
        },
        {
            breakpoint: '2400px',
            numVisible: 6,
            numScroll: 6
        },
        {
            breakpoint: '2000px',
            numVisible: 5,
            numScroll: 5
        },
        {
            breakpoint: '1600px',
            numVisible: 4,
            numScroll: 4
        },
        {
            breakpoint: '1200px',
            numVisible: 3,
            numScroll: 3
        },
        {
            breakpoint: '800px',
            numVisible: 2,
            numScroll: 2
        },
        {
            breakpoint: '400px',
            numVisible: 1,
            numScroll: 1
        }
    ];

    constructor(
        private router: Router,
        private principal: Principal,
        private uaaClient: UaaClient,
    ) {
    }

    ngOnInit() {
        this.loadAll();
    }

    loadAll() {
        this.loadHomePageIntro();
        this.loadHomePageAppList();
        this.loadHomePagePartners();
    }

    loadHomePageIntro() {
        this.uaaClient.get_articles({
            subject1: 'homepage-intro',
        }).subscribe((res) => {
            const result = res.body;
            if (result['status'] === 'ok') {
                const articles = result['value']['results'];
                if (articles && articles.length > 0) {
                    this.homepageIntro = articles[0].content;
                }
            }
        });
    }

    loadHomePagePartners() {
        this.uaaClient.get_articles({
            subject1: 'homepage-partners',
        }).subscribe((res) => {
            const result = res.body;
            if (result['status'] === 'ok') {
                const articles = result['value']['results'];
                if (articles && articles.length > 0) {
                    this.homepagePartners = articles[0].content;
                }
            }
        });
    }

    loadHomePageAppList() {
        this.uaaClient.get_applications({
            subject1: 'homepage',
            with_picture: 0,
        }).subscribe((res) => {
            const result = res.body;
            if (result['status'] === 'ok') {
                const apps = result['value']['results'];
                const navTitle = apps && apps.length > 0 ? apps[0].url : '平台导航';
                this.loadApplications(navTitle);
            }
        });
    }

    loadApplications(navTitle: string) {
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
