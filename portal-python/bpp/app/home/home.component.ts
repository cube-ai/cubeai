import {Component, ViewChild, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ArticleService, ApplicationService} from '../shared';
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
                private articleService: ArticleService,
                private applicationService: ApplicationService,
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
        this.applicationService.query({
            subject1: 'homepage',
        }).subscribe((res) => {
            const navTitle = res.body && res.body.length > 0 ? res.body[0].url : '平台导航';
            if (this.appList) {
                this.appList.loadAll(navTitle);
            }
        });
    }

    loadHomePageIntro() {
        this.articleService.query({
            subject1: 'homepage-intro',
        }).subscribe((res) => {
            if (res.body && res.body.length > 0) {
                this.homepageIntro = res.body[0].content;
            }
        });
    }

    loadHomePagePartners() {
        this.articleService.query({
            subject1: 'homepage-partners',
        }).subscribe((res) => {
            if (res.body && res.body.length > 0) {
                this.homepagePartners = res.body[0].content;
            }
        });
    }

}
