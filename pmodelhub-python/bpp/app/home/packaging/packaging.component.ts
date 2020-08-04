import {Component, OnInit} from '@angular/core';
import {GlobalService, ArticleService} from '../../shared';
import {Location} from '@angular/common';
import {Router} from '@angular/router';

@Component({
    templateUrl: './packaging.component.html',
})
export class PackagingComponent implements OnInit {
    content = 'CubeAI模型打包指南';

    constructor(
        private location: Location,
        private router: Router,
        private globalService: GlobalService,
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
