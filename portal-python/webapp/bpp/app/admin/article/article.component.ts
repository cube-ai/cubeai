import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/Subscription';
import {Principal, User} from '../../shared';
import {Article} from '../../shared/model/article.model';
import {GlobalService, SnackBarService, UaaClient} from '../../shared';
import {v4 as uuid} from 'uuid';

@Component({
    templateUrl: './article.component.html',
    styleUrls: [
        '../admin-datapage.css'
    ]
})
export class ArticleComponent implements OnInit {

    subscription: Subscription;
    user: User;
    article: Article;
    mode: string;
    id: number;
    canEdit = false;
    isEditing = false;

    constructor(
        public globalService: GlobalService,
        private principal: Principal,
        private location: Location,
        private route: ActivatedRoute,
        private router: Router,
        private uaaClient: UaaClient,
        private snackBarService: SnackBarService,
    ) {
    }

    goBack() {
        this.location.back();
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }
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
        this.uaaClient.find_article({
            id: this.id,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.article = res.body['value'];
                } else {
                    this.snackBarService.error('获取文稿出错：' + res.body['value']);
                    this.goBack();
                }
            }, () => {
                this.snackBarService.error('获取文稿出错！');
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
            this.uaaClient.update_article({
                article: this.article,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.goBack();
                    } else {
                        this.snackBarService.error('文稿内容更新失败：' + res.body['value']);
                    }
                }, () => {
                    this.snackBarService.error('文稿内容更新失败！');
                }
            );
        } else {
            this.uaaClient.create_article({
                article: this.article,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.goBack();
                    } else {
                        this.snackBarService.error('新文稿保存失败：' + res.body['value']);
                    }
                }, () => {
                    this.snackBarService.error('新文稿保存失败！');
                }
            );
        }
    }

    enterEdit() {
        this.isEditing = true;
    }

    preview() {
        this.isEditing = false;
    }

}
