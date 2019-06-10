import {Component, AfterViewInit, ViewChild, OnDestroy} from '@angular/core';
import {MatSidenav} from '@angular/material';
import {GlobalService} from '../shared';

@Component({
    templateUrl: './admin.component.html',
})
export class AdminComponent implements AfterViewInit, OnDestroy {
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
