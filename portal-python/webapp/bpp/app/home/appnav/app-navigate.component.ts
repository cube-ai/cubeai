import {Component, ViewChild, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {AppListComponent} from './app-list.component';

@Component({
    templateUrl: './app-navigate.component.html',
})
export class AppNavigateComponent implements OnInit {
    isMobile = window.screen.width < 960;
    @ViewChild(AppListComponent) appList: AppListComponent;
    navTitle: string;

    constructor(
        private route: ActivatedRoute,
        ) {
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.navTitle = params['navTitle'];
            if (this.appList) {
                this.appList.loadAll(this.navTitle);
            }
        });
    }

}
