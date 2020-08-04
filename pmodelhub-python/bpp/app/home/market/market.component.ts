import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, PageEvent} from '@angular/material';
import {ConfirmService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService, GlobalService} from '../../shared';
import {Principal} from '../../shared';
import {Solution} from '../model/solution.model';
import {Star} from '../model/star.model';
import {SolutionService} from '../service/solution.service';
import {StarService} from '../service/star.service';
import {Router} from '@angular/router';

@Component({
    templateUrl: './market.component.html',
})
export class MarketComponent implements OnInit {

    isMobile = window.screen.width < 960;
    filter = '';
    userLogin: string;
    solutions: Solution[] = [];
    staredSolutionUuidList: string[] = [];

    @ViewChild(MatPaginator) paginator: MatPaginator;
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    previousItemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 1;
    previousPage = 1;
    predicate = 'displayOrder';
    reverse = false;

    constructor(
        private globalService: GlobalService,
        private principal: Principal,
        private router: Router,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
        private solutionService: SolutionService,
        private starService: StarService,
    ) {
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.findStaredUuidList();
            this.loadAll();
        });
    }

    findStaredUuidList() {
        if (this.userLogin) {
            this.starService.findStaredUuidList(this.userLogin).subscribe(
                (res) => this.staredSolutionUuidList = res.body
            );
        }

    }

    loadAll() {
        const queryOptions = {
            active: true,
        };
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
            result.push('id' + ',' + (this.reverse ? 'asc' : 'desc'));
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

    onSuccess(data, headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.solutions = data;
    }

    onError(error) {
    }

    viewSolution(solution) {
        this.router.navigate(['/solution/' + solution.uuid]);
    }

    isMyStar(solution: Solution): boolean {
        return this.staredSolutionUuidList.includes(solution.uuid);
    }

    toggleStar(solution: Solution) {
        if (!this.userLogin) {
            const reason = '你尚未登录，请登录后再关注...';
            const redirectUrl = (window.location.pathname + '@' + this.router.url).replace(/\//g, '$');
            window.location.href = '/#/login/' + redirectUrl + '/' + reason;
        }

        if (this.isMyStar(solution)) {
            this.starService.deleteStarByTargetUuid(solution.uuid).subscribe(
                () => {
                    this.solutionService.updateStarCount({
                        id: solution.id,
                    }).subscribe();
                    solution.starCount --;
                    this.findStaredUuidList();
                }
            );
        } else {
            const star = new Star();
            star.targetType = 'AI模型';
            star.targetUuid = solution.uuid;
            this.starService.create(star).subscribe(
                () => {
                    this.solutionService.updateStarCount({
                        id: solution.id,
                    }).subscribe();
                    solution.starCount ++;
                    this.findStaredUuidList();
                }
            );
        }
    }

    gotoMyModels() {
        this.router.navigate(['/personal/' + this.userLogin]);
    }

    gotoPersonal(authorLogin: string) {
        this.router.navigate(['/personal/' + authorLogin]);
    }

    gotoMyTasks() {
        window.location.href = '/ppersonal/#/task';
    }

    gotoMyStars() {
        window.location.href = '/ppersonal/#/star/' + this.userLogin;
    }

    gotoStargazers(solution: Solution) {
        if (solution.starCount > 0) {
            this.router.navigate(['/stargazer/' + solution.uuid + '/' + solution.name]);
        }
    }

    gotoOnboarding() {
        this.router.navigate(['/onboarding']);
    }

}
