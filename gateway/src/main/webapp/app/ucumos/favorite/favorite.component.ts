import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, PageEvent} from '@angular/material';
import {ConfirmService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService, GlobalService} from '../../shared';
import {Principal} from '../../account';
import {SolutionFavorite} from '../model/solution-favorite.model';
import {SolutionFavoriteService} from '../service/solution-favorite.service';
import {Router} from '@angular/router';

@Component({
    templateUrl: './favorite.component.html',
    styleUrls: [
        '../ucumos-datapage.css'
    ]
})
export class FavoriteComponent implements OnInit {

    userLogin: string;
    solutionFavorites: SolutionFavorite[] = [];

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
        private globalService: GlobalService,
        private principal: Principal,
        private router: Router,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
        private solutionFavoriteService: SolutionFavoriteService,
    ) {
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.toggleSideNav(); // 手机屏幕默认隐藏sideNav
        }

        this.userLogin = this.principal.getCurrentAccount().login;
        this.loadAll();
    }

    loadAll() {
        this.solutionFavoriteService.query({
            userLogin: this.userLogin,
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort(),
        }).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
        );
    }

    trackIdentity(index, item: SolutionFavorite) {
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
        this.solutionFavorites = data;
        // TODO: 如因删除等原因致使当前页超出页面总数，此时MatPaginator不会自动跳转至最后一页，而是显示空白页。暂时维持该bug待以后修正。
    }

    private onError(error) {
    }

    deleteFavorite(id) {
        this.solutionFavoriteService.delete(id).subscribe(
            () => {
                this.refresh();
            }
        );
    }

    viewSolution(solutionUuid) {
        this.router.navigate(['/ucumos/solution/' + solutionUuid + '/' + 'view']);
    }

}
