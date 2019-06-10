import {AfterViewInit, Component, OnDestroy, ViewChild} from '@angular/core';
import {MatSidenav} from '@angular/material';
import {GlobalService} from '../shared';

@Component({
    templateUrl: './ucumos.component.html',
})
export class UcumosComponent implements AfterViewInit, OnDestroy {
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
