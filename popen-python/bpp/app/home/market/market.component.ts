import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, PageEvent} from '@angular/material';
import {ConfirmService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService, GlobalService} from '../../shared';
import {Principal} from '../../shared';
import {Ability} from '../model/ability.model';
import {Star} from '../model/star.model';
import {AbilityService} from '../service/ability.service';
import {SolutionService} from '../service/solution.service';
import {StarService} from '../service/star.service';
import {Router} from '@angular/router';

@Component({
    templateUrl: './market.component.html',
})
export class MarketComponent implements OnInit {
    isMobile = window.screen.width < 960;

    filter = '';
    searchStatus = '运行';
    userLogin: string;
    isOperator: boolean;
    abilitys: Ability[] = [];
    staredAbilityUuidList: string[] = [];

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
        private abilityService: AbilityService,
        private solutionService: SolutionService,
        private starService: StarService,
    ) {
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.isOperator = this.principal.hasAuthority('ROLE_OPERATOR');
            this.findStaredUuidList();
            this.loadAll();
        });
    }

    findStaredUuidList() {
        if (this.userLogin) {
            this.starService.findStaredUuidList(this.userLogin).subscribe(
                (res) => this.staredAbilityUuidList = res.body
            );
        }

    }

    loadAll() {
        const queryOptions = {};
        if (!this.isOperator) {
            queryOptions['isPublic'] = true;
        }
        if (this.filter) {
            queryOptions['filter'] = this.filter;
        }
        if (this.searchStatus && this.searchStatus !== 'all') {
            queryOptions['status'] = this.searchStatus;
        }
        queryOptions['page'] = this.page - 1;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.abilityService.query(queryOptions).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
        );
    }

    trackIdentity(index, item: Ability) {
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

    onError(error) {
    }

    onSuccess(data, headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.abilitys = data;
    }

    isMyStar(ability: Ability): boolean {
        return this.staredAbilityUuidList.includes(ability.uuid);
    }

    toggleStar(ability: Ability) {
        if (!this.userLogin) {
            const reason = '你尚未登录，请登录后再关注...';
            const redirectUrl = (window.location.pathname + '@' + this.router.url).replace(/\//g, '$');
            window.location.href = '/#/login/' + redirectUrl + '/' + reason;
        }

        if (this.isMyStar(ability)) {
            this.starService.deleteStarByTargetUuid(ability.uuid).subscribe(
                () => {
                    this.abilityService.updateStarCount({
                        id: ability.id,
                    }).subscribe();
                    ability.starCount --;
                    this.findStaredUuidList();
                }
            );
        } else {
            const star = new Star();
            star.targetType = 'AI开放能力';
            star.targetUuid = ability.uuid;
            this.starService.create(star).subscribe(
                () => {
                    this.abilityService.updateStarCount({
                        id: ability.id,
                    }).subscribe();
                    ability.starCount ++;
                    this.findStaredUuidList();
                }
            );
        }
    }

    viewAbility(ability) {
        this.router.navigate(['/ability/' + ability.uuid]);
    }

    gotoMyAbilitys() {
        this.router.navigate(['/personal/' + this.userLogin]);
    }

    gotoPersonalAbilitys(deployer: string) {
        this.router.navigate(['/personal/' + deployer]);
    }

    gotoPersonalModels(solutionAuthor: string) {
        window.location.href = '/pmodelhub/#/personal/' + solutionAuthor;
    }

    gotoMyTasks() {
        window.location.href = '/ppersonal/#/task';
    }

    gotoMyStars() {
        window.location.href = '/ppersonal/#/star/' + this.userLogin;
    }

    gotoStargazers(ability) {
        if (ability.starCount > 0) {
            this.router.navigate(['/stargazer/' + ability.uuid + '/' + ability.solutionName]);
        }
    }

    gotoDemos() {
        this.router.navigate(['/demo']);
    }

}
