import { Injectable } from '@angular/core';
import {HeaderComponent} from '../../layouts';

@Injectable()
export class GlobalService {

    private header: HeaderComponent;
    private previousUrl: string = null;
    private sideNav = null;
    public lastResetPasswordTime: Date = null;

    constructor(
    ) {}

    getHeader() {
        return this.header;
    }

    setHeader(header: HeaderComponent) {
        this.header = header;
    }

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

    setSideNav(sideNav) {
        this.sideNav = sideNav;
    }

}
