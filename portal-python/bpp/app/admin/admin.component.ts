import {Component, AfterViewInit, ViewChild, OnDestroy, OnInit} from '@angular/core';
import {MatSidenav} from '@angular/material';
import {Router} from '@angular/router';
import {GlobalService, Principal} from '../shared';

@Component({
    templateUrl: './admin.component.html',
})
export class AdminComponent implements AfterViewInit, OnInit, OnDestroy {
    @ViewChild(MatSidenav) sideNav: MatSidenav;

    constructor(
        private principal: Principal,
        private router: Router,
        private globalService: GlobalService,
    ) {
    }

    ngAfterViewInit() {
        this.globalService.setSideNav(this.sideNav);
        if (window.screen.width < 960) {
            this.sideNav.toggle(false); // 手机屏幕默认隐藏sideNav
        }
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            if (this.principal.hasAuthority('ROLE_ADMIN')) {
                this.router.navigate(['/admin/user-management']);
            } else if (this.principal.hasAuthority('ROLE_CONTENT')) {
                this.router.navigate(['/admin/bulletin']);
            } else if (this.principal.hasAuthority('ROLE_APPLICATION')) {
                this.router.navigate(['/admin/application']);
            }
        });
    }

    ngOnDestroy() {
        this.globalService.setSideNav(null);
    }
}
