import { Component, OnInit } from '@angular/core';
import {GlobalService} from '../../shared';

@Component({
    selector: 'jhi-docs',
    templateUrl: './docs.component.html',
    styleUrls: [
        '../admin-datapage.css',
    ],
})
export class JhiDocsComponent implements OnInit {
    constructor(
        private globalService: GlobalService,
    ) {
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }
    }
}
