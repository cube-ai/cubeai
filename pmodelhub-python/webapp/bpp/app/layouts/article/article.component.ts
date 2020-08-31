import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/Subscription';
import {Principal, SnackBarService, HttpService} from '../../shared';

@Component({
    templateUrl: './article.component.html',
})
export class ArticleComponent implements OnInit, OnDestroy {

    subscription: Subscription;
    content: string;

    constructor(
        private principal: Principal,
        private location: Location,
        private route: ActivatedRoute,
        private router: Router,
        private http: HttpService,
        private snackBarService: SnackBarService,
    ) {
    }

    goBack() {
        this.location.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            const body = {
                action: 'get_articles',
                args: {
                    subject1: params['subject'],
                },
            };
            this.http.post('uaa', body).subscribe((res) => {
                if (res.body['status'] === 'ok' && res.body['value']['total'] > 0) {
                    this.content = res.body['value']['results'][0].content;
                } else {
                    this.snackBarService.error('页面不存在！');
                    this.goBack();
                }
            });
        });
    }

}
