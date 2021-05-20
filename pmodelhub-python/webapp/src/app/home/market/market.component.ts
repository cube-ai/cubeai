import {Component, OnInit} from '@angular/core';
import {Principal, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {UmmClient} from '../service/umm_client.service';
import {Solution} from '../model/solution.model';
import {Star} from '../model/star.model';
import {Router} from '@angular/router';
import {LazyLoadEvent} from 'primeng/api';
import {MessageService} from 'primeng/api';

@Component({
    templateUrl: './market.component.html',
})
export class MarketComponent implements OnInit {
    userLogin: string;
    solutions: Solution[] = [];
    staredSolutionUuidList: string[] = [];

    filter = '';
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 0;
    predicate = 'displayOrder';
    reverse = false;
    loading = false;
    layout = 'list';
    pagePrefix = 'solution_market';

    predicates = [
        {label: '默认', value: 'displayOrder'},
        {label: '最新', value: 'id'},
        {label: '最火', value: 'starCount'},
    ];

    constructor(
        private principal: Principal,
        private router: Router,
        private ummClient: UmmClient,
        private messageService: MessageService,
    ) {
    }

    ngOnInit() {
        this.restorePageState();
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.findStaredUuidList();
        });
    }

    findStaredUuidList() {
        if (this.userLogin) {
            this.ummClient.get_user_stared_uuid_list({
                'userLogin': this.userLogin,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.staredSolutionUuidList = res.body['value'];
                    }
                }
            );
        }

    }

    restorePageState() {
        let temp = window.localStorage.getItem(this.pagePrefix + '_itemsPerPage');
        if (temp !== null) {
            this.itemsPerPage = parseInt(temp, 10);
        }

        temp = window.localStorage.getItem(this.pagePrefix + '_page');
        if (temp !== null) {
            this.page = parseInt(temp, 10);
        }

        temp = window.localStorage.getItem(this.pagePrefix + '_predicate');
        if (temp !== null) {
            this.predicate = temp;
        }

        temp = window.localStorage.getItem(this.pagePrefix + '_reverse');
        if (temp !== null) {
            this.reverse = temp === 'true';
        }

        temp = window.localStorage.getItem(this.pagePrefix + '_layout');
        if (temp !== null) {
            this.layout = temp;
        }
    }

    backupPageState() {
        window.localStorage.setItem(this.pagePrefix + '_itemsPerPage', this.itemsPerPage.toString(10));
        window.localStorage.setItem(this.pagePrefix + '_page', this.page.toString(10));
        window.localStorage.setItem(this.pagePrefix + '_predicate', this.predicate);
        window.localStorage.setItem(this.pagePrefix + '_reverse', this.reverse.toString());
        window.localStorage.setItem(this.pagePrefix + '_layout', this.layout);
    }

    loadData() {
        this.backupPageState();

        const queryOptions = {
            active: true,
        };
        if (!!this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.loading = true;
        this.ummClient.get_solutions(queryOptions).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.loading = false;
                    this.totalItems = res.body['value']['total'];
                    this.solutions = res.body['value']['results'];
                } else {
                    this.loading = false;
                    this.messageService.add({severity:'error', detail:'获取模型列表失败！'});
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

    changeLayout(event) {
        this.layout = event.layout;
        this.backupPageState();
    }

    viewSolution(solution) {
        this.router.navigate(['/solution/' + solution.uuid]);
    }

    isMyStar(solution: Solution): boolean {
        return this.staredSolutionUuidList.includes(solution.uuid);
    }

    toggleStar(solution: Solution) {
        if (!this.userLogin) {
            window.localStorage.setItem('loginReason', '你尚未登录，请登录后再关注...');
            window.localStorage.setItem('loginRedirectUrl', window.location.pathname + '#' + this.router.url);
            window.location.href = '/#/login';
        }

        if (this.isMyStar(solution)) {
            this.ummClient.delete_star_by_target_uuid({
                targetUuid: solution.uuid,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.ummClient.update_solution_star_count({
                            solutionId: solution.id,
                        }).subscribe();
                        solution.starCount --;
                        this.findStaredUuidList();
                    }
                }
            );
        } else {
            const star = new Star();
            star.targetUuid = solution.uuid;
            this.ummClient.create_star({
                star,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.ummClient.update_solution_star_count({
                            solutionId: solution.id,
                        }).subscribe();
                        solution.starCount ++;
                        this.findStaredUuidList();
                    }
                }
            );
        }
    }

    gotoPersonal(authorLogin: string) {
        this.router.navigate(['/personal/' + authorLogin]);
    }

    gotoStargazers(solution: Solution) {
        if (solution.starCount > 0) {
            this.router.navigate(['/stargazer/' + solution.uuid + '/' + solution.name]);
        }
    }

}
