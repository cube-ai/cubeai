import {Component, OnInit, ViewChild} from '@angular/core';
import {ConfirmService, SnackBarService, GlobalService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {Article} from '../../shared/model/article.model';
import {Router} from '@angular/router';
import {Principal, User, UaaClient} from '../../shared';
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
        public globalService: GlobalService,
        private router: Router,
        private principal: Principal,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
        private uaaClient: UaaClient,
    ) {
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }

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

        this.uaaClient.get_articles(queryOptions).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.totalItems = res.body['value']['total'];
                    this.articles = res.body['value']['results'];
                }
            }
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
        this.confirmService.ask('确定要删除该文稿？').then((confirm) => {
            if (confirm) {
                this.uaaClient.delete_article({
                    id: article.id,
                }).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.refresh();
                        } else {
                            this.snackBarService.error('删除失败：' + res.body['value']);
                        }
                    }, () => {
                        this.snackBarService.error('删除失败！可能是网络不通...');
                    }
                );
            }
        });
    }

}
