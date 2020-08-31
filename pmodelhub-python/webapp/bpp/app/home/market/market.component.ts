import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, PageEvent} from '@angular/material';
import {ConfirmService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService, GlobalService, HttpService} from '../../shared';
import {Principal} from '../../shared';
import {Solution} from '../model/solution.model';
import {Star} from '../model/star.model';
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
        private http: HttpService,
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
            const body = {
                action: 'get_user_stared_uuid_list',
                args: {
                    'userLogin': this.userLogin,
                },
            };
            this.http.post('umm', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.staredSolutionUuidList = res.body['value'];
                    }
                }
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

        const body = {
            action: 'get_solutions',
            args: queryOptions,
        };
        this.http.post('umm', body).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.totalItems = res.body['value']['total'];
                    this.solutions = res.body['value']['results'];
                }
            }
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
            const body = {
                action: 'delete_star_by_target_uuid',
                args: {
                    targetUuid: solution.uuid,
                },
            };
            this.http.post('umm', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        const body1 = {
                            action: 'update_solution_star_count',
                            args: {
                                solutionId: solution.id,
                            },
                        };
                        this.http.post('umm', body1).subscribe();
                        solution.starCount --;
                        this.findStaredUuidList();
                    }
                }
            );
        } else {
            const star = new Star();
            star.targetType = 'AI模型';
            star.targetUuid = solution.uuid;

            const body = {
                action: 'create_star',
                args: {
                    star,
                },
            };
            this.http.post('umm', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        const body1 = {
                            action: 'update_solution_star_count',
                            args: {
                                solutionId: solution.id,
                            },
                        };
                        this.http.post('umm', body1).subscribe();
                        solution.starCount ++;
                        this.findStaredUuidList();
                    }
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
