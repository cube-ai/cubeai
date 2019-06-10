import {Component, OnInit} from '@angular/core';
import {ArticleService} from '../../admin';

@Component({
    templateUrl: './packaging.component.html',
    styleUrls: [
        '../ucumos-datapage.css'
    ]
})
export class PackagingComponent implements OnInit {
    content = 'CubeAI模型打包指南';

    constructor(
        private articleService: ArticleService,
    ) {
    }

    ngOnInit() {
        this.articleService.query({
            subject1: 'model-packaging',
        }).subscribe((res) => {
            if (res.body && res.body.length > 0) {
                this.content = res.body[0].content;
            }
        });
    }
}
