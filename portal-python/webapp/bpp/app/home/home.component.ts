import {Component, ViewChild, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {HttpService} from '../shared';
import {AppListComponent} from './appnav/app-list.component';

@Component({
    templateUrl: './home.component.html',
    styleUrls: [
        'home.css',
    ]
})
export class HomeComponent implements OnInit {
    @ViewChild(AppListComponent) appList: AppListComponent;
    homepageIntro = '';
    homepagePartners = '';

    constructor(private router: Router,
                private http: HttpService,
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

    loadHomePageAppList() {
        const body = {
            action: 'get_applications',
            args: {
                subject1: 'homepage',
                with_picture: 0,
            },
        };
        this.http.post('uaa', body).subscribe((res) => {
            const result = res.body;
            if (result['status'] === 'ok') {
                const applications = result['value']['results'];
                const navTitle = applications && applications.length > 0 ? applications[0].url : '平台导航';
                if (this.appList) {
                    this.appList.loadAll(navTitle);
                }
            }
        });

    }

    loadHomePageIntro() {
        const body = {
            action: 'get_articles',
            args: {
                subject1: 'homepage-intro',
            },
        };
        this.http.post('uaa', body).subscribe((res) => {
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
        const body = {
            action: 'get_articles',
            args: {
                subject1: 'homepage-partners',
            },
        };
        this.http.post('uaa', body).subscribe((res) => {
            const result = res.body;
            if (result['status'] === 'ok') {
                const articles = result['value']['results'];
                if (articles && articles.length > 0) {
                    this.homepagePartners = articles[0].content;
                }
            }
        });
    }

}
