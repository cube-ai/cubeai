import {Component, ViewChild, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {UaaClient} from '../shared';
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

    loadHomePageAppList() {
        this.uaaClient.get_applications({
            subject1: 'homepage',
            with_picture: 0,
        }).subscribe((res) => {
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

}
