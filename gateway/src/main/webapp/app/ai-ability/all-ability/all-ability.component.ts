import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, PageEvent} from '@angular/material';
import {ConfirmService, GlobalService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService} from '../../shared';
import {Principal} from '../../account';
import {Ability} from '../model/ability.model';
import {AbilityService} from '../service/ability.service';
import {Router} from '@angular/router';

@Component({
    templateUrl: './all-ability.component.html',
    styleUrls: [
        '../ai-ability-datapage.css'
    ]
})
export class AllAbilityComponent implements OnInit {
    filter = '';
    abilities: Ability[] = [];

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
        private abilityService: AbilityService,
    ) {
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }
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

        this.abilityService.queryAll(queryOptions).subscribe(
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
        this.abilities = data;
    }

    private onError(error) {
    }

    viewSolution(solutionUuid) {
        this.router.navigate(['/ucumos/solution/' + solutionUuid + '/' + 'view']);
    }

    viewAbility(ability) {
        this.router.navigate(['/ai-ability/ability/' + ability.uuid + '/' + 'view']);
    }

    manageAbility(ability) {
        this.router.navigate(['/ai-ability/ability/' + ability.uuid + '/' + 'edit']);
    }

    genAbilityUrl(ability: Ability): string {
        return 'POST ' + location.protocol + '//' + location.host + '/ability/model/' + ability.uuid + '/{model-method}';
    }

}
