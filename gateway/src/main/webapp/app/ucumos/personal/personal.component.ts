import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, PageEvent} from '@angular/material';
import {ConfirmService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService, GlobalService} from '../../shared';
import {Principal} from '../../account';
import {Solution} from '../model/solution.model';
import {SolutionFavorite} from '../model/solution-favorite.model';
import {SolutionService} from '../service/solution.service';
import {SolutionFavoriteService} from '../service/solution-favorite.service';
import {Router} from '@angular/router';

@Component({
    templateUrl: './personal.component.html',
    styleUrls: [
        '../ucumos-datapage.css'
    ]
})
export class PersonalComponent implements OnInit {

    filter = '';
    searchName = '';
    searchTag = '';
    searchModelType = '';
    searchToolkitType = '';
    modelStatus = '未上架';

    userLogin: string;
    solutions: Solution[] = [];
    favoriteSolutionUuidList: string[] = [];

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
        private router: Router,
        private principal: Principal,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
        private solutionService: SolutionService,
        private solutionFavoriteService: SolutionFavoriteService,
    ) {
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }

        this.userLogin = this.principal.getCurrentAccount().login;
        this.findFavoriteSolutionUuidList();
        this.loadAll();
    }

    findFavoriteSolutionUuidList() {
        this.solutionFavoriteService.findFavoriteSolutionUuidList(this.userLogin).subscribe(
            (res) => this.favoriteSolutionUuidList = res.body
        );
    }

    loadAll() {
        if (this.modelStatus === '未上架') {
            this.loadUnpublished();
        }  else if (this.modelStatus === '申请上架') {
            this.loadWaitPublish();
        } else if (this.modelStatus === '已上架') {
            this.loadPublished();
        }  else if (this.modelStatus === '申请下架') {
            this.loadWaitUnpublish();
        } else if (this.modelStatus === '回收站') {
            this.loadDeactived();
        }
    }

    loadUnpublished() {
        const queryOptions = {
            active: true,
            publishStatus: '下架',
            publishRequest: '无申请',
            authorLogin: this.userLogin,
        };
        if (this.searchName) {
            queryOptions['name'] = this.searchName;
        }
        if (this.searchTag) {
            queryOptions['tag'] = this.searchTag;
        }
        if (this.searchModelType && this.searchModelType !== 'all') {
            queryOptions['modelType'] = this.searchModelType;
        }
        if (this.searchToolkitType && this.searchToolkitType !== 'all') {
            queryOptions['toolkitType'] = this.searchToolkitType;
        }
        if (this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page - 1;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.solutionService.query(queryOptions).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
        );
    }

    loadWaitPublish() {
        const queryOptions = {
            active: true,
            publishStatus: '下架',
            publishRequest: '申请上架',
            authorLogin: this.userLogin,
        };
        if (this.searchName) {
            queryOptions['name'] = this.searchName;
        }
        if (this.searchTag) {
            queryOptions['tag'] = this.searchTag;
        }
        if (this.searchModelType && this.searchModelType !== 'all') {
            queryOptions['modelType'] = this.searchModelType;
        }
        if (this.searchToolkitType && this.searchToolkitType !== 'all') {
            queryOptions['toolkitType'] = this.searchToolkitType;
        }
        if (this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page - 1;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.solutionService.query(queryOptions).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
        );
    }

    loadPublished() {
        const queryOptions = {
            active: true,
            publishStatus: '上架',
            publishRequest: '无申请',
            authorLogin: this.userLogin,
        };
        if (this.searchName) {
            queryOptions['name'] = this.searchName;
        }
        if (this.searchTag) {
            queryOptions['tag'] = this.searchTag;
        }
        if (this.searchModelType && this.searchModelType !== 'all') {
            queryOptions['modelType'] = this.searchModelType;
        }
        if (this.searchToolkitType && this.searchToolkitType !== 'all') {
            queryOptions['toolkitType'] = this.searchToolkitType;
        }
        if (this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page - 1;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.solutionService.query(queryOptions).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
        );
    }

    loadWaitUnpublish() {
        const queryOptions = {
            active: true,
            publishStatus: '上架',
            publishRequest: '申请下架',
            authorLogin: this.userLogin,
        };
        if (this.searchName) {
            queryOptions['name'] = this.searchName;
        }
        if (this.searchTag) {
            queryOptions['tag'] = this.searchTag;
        }
        if (this.searchModelType && this.searchModelType !== 'all') {
            queryOptions['modelType'] = this.searchModelType;
        }
        if (this.searchToolkitType && this.searchToolkitType !== 'all') {
            queryOptions['toolkitType'] = this.searchToolkitType;
        }
        if (this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page - 1;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.solutionService.query(queryOptions).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
        );
    }

    loadDeactived() {
        const queryOptions = {
            active: false,
            publishStatus: '下架', // 除了单独使用uuid查询外，查询条件中必须携带publishStatus字段，便于权限控制
            authorLogin: this.userLogin,
        };
        if (this.searchName) {
            queryOptions['name'] = this.searchName;
        }
        if (this.searchTag) {
            queryOptions['tag'] = this.searchTag;
        }
        if (this.searchModelType && this.searchModelType !== 'all') {
            queryOptions['modelType'] = this.searchModelType;
        }
        if (this.searchToolkitType && this.searchToolkitType !== 'all') {
            queryOptions['toolkitType'] = this.searchToolkitType;
        }
        if (this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page - 1;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.solutionService.query(queryOptions).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
        );
    }

    trackIdentity(index, item: Solution) {
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
        this.solutions = data;
    }

    private onError(error) {
    }

    getRatingWidth(rating: number) {
        if (rating !== null) {
            const starWidthPx = Math.round((rating / 5) * 60);
            return starWidthPx + 'px';
        } else {
            return 0;
        }
    }

    isMyFavorite(solution: Solution): boolean {
        return this.favoriteSolutionUuidList.includes(solution.uuid);
    }

    toggleFavorite(solution: Solution) {

        if (this.isMyFavorite(solution)) {
            this.solutionFavoriteService.deleteSolutionFavoriteBySolutionUuid(solution.uuid).subscribe(
                () => {
                    this.findFavoriteSolutionUuidList();
                }
            );
        } else {
            const solutionFavorite = new SolutionFavorite();
            solutionFavorite.solutionUuid = solution.uuid;
            solutionFavorite.solutionName = solution.name;
            solutionFavorite.solutionAuthor = solution.authorLogin;
            solutionFavorite.solutionCreatedDate = solution.createdDate;
            this.solutionFavoriteService.create(solutionFavorite).subscribe(
                () => {
                    this.findFavoriteSolutionUuidList();
                }
            );
        }
    }

    viewSolution(solution) {
        if (this.modelStatus === '未上架' || this.modelStatus === '已上架') {
            this.router.navigate(['/ucumos/solution/' + solution.uuid + '/' + 'edit']);
        } else {
            this.router.navigate(['/ucumos/solution/' + solution.uuid + '/' + 'view']);
        }
    }

    onboarding() {
        this.router.navigate(['/ucumos/onboarding']);
    }

}
