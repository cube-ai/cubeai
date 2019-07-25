import {Component, OnInit} from '@angular/core';
import {ArticleService} from '../../admin';
import {GlobalService} from '../../shared';

@Component({
    templateUrl: './packaging.component.html',
    styleUrls: [
        '../ucumos-datapage.css'
    ]
})
export class PackagingComponent implements OnInit {
    content = 'CubeAI模型打包指南';

    constructor(
        private globalService: GlobalService,
        private articleService: ArticleService,
    ) {
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }

        this.articleService.query({
            subject1: 'model-packaging',
        }).subscribe((res) => {
            if (res.body && res.body.length > 0) {
                this.content = res.body[0].content;
            }
        });
    }
}
