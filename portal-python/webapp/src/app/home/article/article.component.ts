import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '@angular/common';
import {MessageService} from 'primeng/api';
import {Principal, UaaClient} from '../../shared';

@Component({
    templateUrl: './article.component.html',
})
export class ArticleComponent implements OnInit {
    content: string;

    constructor(
        private principal: Principal,
        private location: Location,
        private route: ActivatedRoute,
        private router: Router,
        private uaaClient: UaaClient,
        private messageService: MessageService,
    ) {
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.uaaClient.get_articles({
                subject1: params['subject'],
            }).subscribe((res) => {
                if (res.body['status'] === 'ok' && res.body['value']['total'] > 0) {
                    this.content = res.body['value']['results'][0].content;
                } else {
                    this.messageService.add({severity:'warn', detail:'页面内容不存在！'});
                    // this.goBack();
                }
            });
        });
    }

    goBack() {
        this.location.back();
    }

}
