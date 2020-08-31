import {AfterViewInit, OnDestroy, Component, ViewChild} from '@angular/core';
import {GlobalService} from '../shared';
import {MatSidenav} from '@angular/material';

@Component({
    templateUrl: './message.component.html',
})
export class MessageComponent implements AfterViewInit, OnDestroy {
    @ViewChild(MatSidenav) sideNav: MatSidenav;

    constructor(
        private globalService: GlobalService,
    ) {
    }

    ngAfterViewInit() {
        this.globalService.setSideNav(this.sideNav);
        if (window.screen.width < 960) {
            this.sideNav.toggle(false); // 手机屏幕默认隐藏sideNav
        }
    }

    ngOnDestroy() {
        this.globalService.setSideNav(null);
    }
}
