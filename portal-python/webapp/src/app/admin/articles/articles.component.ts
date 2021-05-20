import {Component, OnInit} from '@angular/core';
import {GlobalService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {Article} from '../../shared/model/article.model';
import {Router} from '@angular/router';
import {Principal, User, UaaClient} from '../../shared';
import { LazyLoadEvent } from 'primeng/api';
import {MessageService} from 'primeng/api';
import {ConfirmationService} from 'primeng/api';

@Component({
    templateUrl: './articles.component.html',
})
export class ArticlesComponent implements OnInit {
    user: User;
    articles: Article[] = [];

    filter = '';
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 0;
    predicate = 'id';
    reverse = false;
    loading = false;

    constructor(
        public globalService: GlobalService,
        private router: Router,
        private principal: Principal,
        private uaaClient: UaaClient,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
    ) {
    }

    ngOnInit() {
        this.user = this.principal.getCurrentAccount();
    }

    loadData() {
        const queryOptions = {};
        if (this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.loading = true;
        this.uaaClient.get_articles(queryOptions).subscribe(
            (res) => {
                this.loading = false;
                if (res.body['status'] === 'ok') {
                    this.totalItems = res.body['value']['total'];
                    this.articles = res.body['value']['results'];
                } else {
                    this.messageService.add({severity:'error', detail:'获取文稿列表失败！'});
                }
            }, () => {
                this.loading = false;
                this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
            });
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id' + ',' + (this.reverse ? 'asc' : 'desc'));
        }
        return result;
    }

    reloadPage(event: LazyLoadEvent) {
        if (event.rows) {
            this.itemsPerPage = event.rows;
            this.page = event.first / event.rows;
        }

        if (event.sortField) {
            this.predicate = event.sortField;
            this.reverse = event.sortOrder > 0;
        }

        this.loadData();
    }

    canCreate(): boolean {
        return this.user.authorities.includes('ROLE_CONTENT');
    }

    canEdit(article: Article): boolean {
        return this.user.authorities.includes('ROLE_CONTENT');
    }

    canDelete(article: Article): boolean {
        return this.user.authorities.includes('ROLE_CONTENT');
    }

    createArticle() {
        this.router.navigate(['/admin/article/create/0']);
    }

    viewArticle(article: Article) {
        this.router.navigate(['/admin/article/view/' + article.id]);
    }

    editArticle(article: Article) {
        this.router.navigate(['/admin/article/edit/' + article.id]);
    }

    deleteArticle(article: Article) {
        this.confirmationService.confirm({
            target: event.target,
            message: '确定要删除该文稿： ' + article.title + ' ？',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: '是',
            rejectLabel: '否',
            accept: () => {
                this.uaaClient.delete_article({
                    id: article.id,
                }).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.loadData();
                        } else {
                            this.messageService.add({severity:'error', detail:'删除失败'});
                        }
                    }, () => {
                        this.messageService.add({severity:'error', detail:'网络或服务器故障'});
                    }
                );
            },
            reject: () => {}
        });
    }


}


