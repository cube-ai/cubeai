import { Injectable } from '@angular/core';

@Injectable()
export class GlobalService {

    private header = null;
    private previousUrl: string = null;
    private sideNavStatus = null;
    public lastResetPasswordTime: Date = null;

    constructor(
    ) {}

    setHeader(header: any) {
        this.header = header;
    }

    getHeader(): any {
        return this.header;
    }

    storeUrl(url: string) {
        this.previousUrl = url;
    }

    getUrl() {
        return this.previousUrl;
    }

    setSideNavStatus(sideNavStatus) {
        this.sideNavStatus = sideNavStatus;
    }

    toggleSideNav() {
        if (this.sideNavStatus) {
            this.sideNavStatus['openMenu'] = !this.sideNavStatus['openMenu'];
        }
    }

    openSideBar() {
        if (this.sideNavStatus) {
            this.sideNavStatus['openPopup'] = true;
        }
    }
}
