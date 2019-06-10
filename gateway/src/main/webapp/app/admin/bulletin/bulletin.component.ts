import {Component, OnInit, ViewChild} from '@angular/core';
import {ConfirmService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService} from '../../shared';
import {ArticleService} from '../article/article.service';
import {Article} from '../article/article.model';
import {Router} from '@angular/router';
import {Principal, User} from '../../account';
import {MatPaginator, PageEvent} from '@angular/material';

@Component({
    templateUrl: './bulletin.component.html',
    styleUrls: [
        '../admin-datapage.css'
    ]
})
export class BulletinComponent implements OnInit {
    user: User;
    articles: Article[] = [];

    filter = '';
    @ViewChild(MatPaginator) paginator: MatPaginator;
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    previousItemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 1;
    previousPage = 1;
    predicate = 'id';
    reverse = false;

    constructor(
        private router: Router,
        private principal: Principal,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
        private articleService: ArticleService,
    ) {
    }

    ngOnInit() {
        this.user = this.principal.getCurrentAccount();
        this.loadAll();
    }

    loadAll() {
        const queryOptions = {};
        if (this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page - 1;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.articleService.query(queryOptions).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
        );
    }

    trackIdentity(index, item: Article) {
        return item.id;
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    reloadPage(pageEvent: PageEvent) {
        this.itemsPerPage = pageEvent.pageSize;
        this.page = pageEvent.pageIndex + 1;

        if (this.previousPage !== this.page) {
            this.previousPage = this.page;
            this.transition();
        }

        if (this.itemsPerPage !== this.previousItemsPerPage) {
            this.previousItemsPerPage = this.itemsPerPage;
            this.transition();
        }
    }

    transition() {
        this.refresh();
    }

    refresh() {
        this.loadAll();
    }

    private onSuccess(data, headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.articles = data;
    }

    private onError(error) {
    }

    canCreate(): boolean {
        return this.user.authorities.includes('ROLE_ADMIN');
    }

    canEdit(article: Article): boolean {
        return this.user.authorities.includes('ROLE_ADMIN');
    }

    canDelete(article: Article): boolean {
        return this.user.authorities.includes('ROLE_ADMIN');
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
        this.confirmService.ask('确定要删除该文稿？').then((confirm) => {
            if (confirm) {
                this.articleService.delete(article.id).subscribe(
                    () => {
                        this.refresh();
                    }
                );
            }
        });
    }

}
