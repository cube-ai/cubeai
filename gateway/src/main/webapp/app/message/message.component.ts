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
    }

    ngOnDestroy() {
        this.globalService.setSideNav(null);
    }
}
