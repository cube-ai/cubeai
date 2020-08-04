import { Injectable } from '@angular/core';

@Injectable()
export class GlobalService {

    private previousUrl: string = null;
    private sideNav = null;
    public lastResetPasswordTime: Date = null;

    constructor(
    ) {}

    storeUrl(url: string) {
        this.previousUrl = url;
    }

    getUrl() {
        return this.previousUrl;
    }

    toggleSideNav() {
        if (this.sideNav) {
            this.sideNav.toggle();
        }
    }

    closeSideNav() {
        if (this.sideNav) {
            this.sideNav.toggle(false);
        }
    }

    setSideNav(sideNav) {
        this.sideNav = sideNav;
    }

    getSideNav(): any {
        return this.sideNav;
    }

}
