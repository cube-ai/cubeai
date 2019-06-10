import {AfterViewInit, Component, OnDestroy, ViewChild} from '@angular/core';
import {MatSidenav} from '@angular/material';
import {GlobalService} from '../shared';

@Component({
    templateUrl: './ai-ability.component.html',
})
export class AiAbilityComponent implements AfterViewInit, OnDestroy {
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
