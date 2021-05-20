import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs';
import {Principal, User} from '../../shared';
import {Article} from '../../shared/model/article.model';
import {GlobalService, UaaClient} from '../../shared';
import {v4 as uuid} from 'uuid';
import {MessageService} from 'primeng/api';

@Component({
    templateUrl: './article.component.html',
})
export class ArticleComponent implements OnInit {

    subscription: Subscription;
    user: User;
    article: Article;
    mode: string;
    headline: string;
    id: number;
    isEditing = false;

    editMode = 'markdown';
    editModes = [
        {label: 'mkdown', value: 'markdown'},
        {label: 'HTML', value: 'html'},
    ];

    constructor(
        public globalService: GlobalService,
        private principal: Principal,
        private location: Location,
        private route: ActivatedRoute,
        private router: Router,
        private uaaClient: UaaClient,
        private messageService: MessageService,
    ) {
    }

    goBack() {
        this.location.back();
    }

    ngOnInit() {
        this.user = this.principal.getCurrentAccount() ? this.principal.getCurrentAccount() : null;
        this.subscription = this.route.params.subscribe((params) => {
            this.mode = params['mode'];
            this.id = params['id'];

            if (!this.user.authorities.includes('ROLE_CONTENT') && this.mode === 'create') {
                this.messageService.add({severity:'error', detail:'你没有权限起草文稿！'});
                this.goBack();
            }

            if (!this.user.authorities.includes('ROLE_CONTENT') && this.mode === 'edit') {
                this.messageService.add({severity:'error', detail:'你没有权限编辑文稿！'});
                this.mode = 'view';
            }

            if (this.mode === 'create') {
                this.createArticle();
            } else {
                this.loadArticle();
            }

            switch (this.mode) {
                case 'create':
                    this.headline = '新建文稿';
                    break;
                case 'edit':
                    this.headline = '编辑文稿';
                    break;
                case 'view':
                    this.headline = '查看文稿';
                    break;
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
                    this.editMode = this.article.content.startsWith('<') ? 'html' : 'markdown';
                } else {
                    this.messageService.add({severity:'error', detail:'获取文稿出错！'});
                    this.goBack();
                }
            }, () => {
                this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
                this.goBack();
            }
        );
    }

    createArticle() {
        this.article = new Article();
        this.article.uuid = uuid().replace(/-/g, '').toLowerCase();
        this.article.authorLogin = this.user.login;
        this.article.authorName = this.user.fullName;
        this.article.title = '';
        this.article.content = '';
        this.article.displayOrder = 0;
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
                        this.messageService.add({severity:'error', detail:'文稿内容更新失败！'});
                    }
                }, () => {
                    this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
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
                        this.messageService.add({severity:'error', detail:'新文稿保存失败！'});
                    }
                }, () => {
                    this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
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
