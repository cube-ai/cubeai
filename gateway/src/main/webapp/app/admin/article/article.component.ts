import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/Subscription';
import {Principal, User} from '../../account';
import {Article} from './article.model';
import {ArticleService} from './article.service';
import {SnackBarService} from '../../shared';
import {v4 as uuid} from 'uuid';

@Component({
    templateUrl: './article.component.html',
    styleUrls: [
        '../admin-datapage.css'
    ]
})
export class ArticleComponent implements OnInit, OnDestroy {

    subscription: Subscription;
    user: User;
    article: Article;
    mode: string;
    id: number;
    canEdit = false;
    isEditing = false;

    constructor(
        private principal: Principal,
        private location: Location,
        private route: ActivatedRoute,
        private router: Router,
        private articleService: ArticleService,
        private snackBarService: SnackBarService,
    ) {
    }

    goBack() {
        this.location.back();
    }

    ngOnDestroy() {
        if (this.article && this.canEdit) {
            this.saveArticle();
        }
        this.subscription.unsubscribe();
    }

    ngOnInit() {
        this.user = this.principal.getCurrentAccount() ? this.principal.getCurrentAccount() : null;
        this.subscription = this.route.params.subscribe((params) => {
            this.mode = params['mode'];
            this.id = params['id'];

            if (!this.user.authorities.includes('ROLE_CONTENT') && this.mode === 'create') {
                this.snackBarService.error('你没有权限起草文稿！');
                this.goBack();
            }

            if (!this.user.authorities.includes('ROLE_CONTENT') && this.mode === 'edit') {
                this.snackBarService.error('你没有权限编辑文稿！');
                this.mode = 'view';
            }

            this.canEdit = this.mode === 'create' || this.mode === 'edit';

            if (this.mode === 'create') {
                this.createArticle();
            } else {
                this.loadArticle();
            }
        });
    }

    loadArticle() {
        this.articleService.find(this.id).subscribe(
            (res) => {
                this.article = res.body;
            }, () => {
                this.snackBarService.error('文稿不存在！');
                this.goBack();
            }
        );
    }

    createArticle() {
        this.article = new Article();
        this.article.uuid = uuid().replace(/-/g, '').toLowerCase();
        this.article.authorLogin = this.user.login;
        this.article.authorName = this.user.fullName;
        this.article.title = '新文稿';
        this.article.content = '在这里写入正文...';
        this.article.createdDate = new Date();
        this.article.modifiedDate = new Date();
    }

    saveArticle() {
        if (this.article.id) {
            this.article.modifiedDate = new Date();
            this.articleService.update(this.article).subscribe(
                (res) => {
                    this.article = res.body;
                    this.isEditing = false;
                }, () => {
                    this.snackBarService.error('文稿内容更新失败！');
                }
            );
        } else {
            this.articleService.create(this.article).subscribe(
                (res) => {
                    this.article = res.body;
                    this.isEditing = false;
                }, () => {
                    this.snackBarService.error('新文稿保存失败！');
                }
            );
        }
    }

    enterEdit() {
        this.isEditing = true;
    }

}
