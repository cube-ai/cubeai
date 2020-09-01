import {Component, OnInit, ViewChild} from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import {MatPaginator, PageEvent} from '@angular/material';
import {Principal, ConfirmService, SnackBarService, GlobalService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {UmmClient} from '../';
import {Location} from '@angular/common';
import {Ability} from '../model/ability.model';
import {Star} from '../model/star.model';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    templateUrl: './personal.component.html',
})
export class PersonalComponent implements OnInit {
    isMobile = window.screen.width < 960;

    subscription: Subscription;
    filter = '';
    userLogin = '';  // 本人
    deployer = '';  // 当前查看的目标部署人的用户名
    isMyself: boolean;
    isPublic = true;
    abilitys: Ability[] = [];
    staredAbilityUuidList: string[] = [];

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
        private location: Location,
        private route: ActivatedRoute,
        private router: Router,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
        private ummClient: UmmClient,
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.deployer = params['deployer'];
            if (this.deployer === 'null') {
                this.goBack();
            }
        });

        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.isMyself = this.userLogin === this.deployer;
            this.findStaredUuidList();
            this.loadAll();
        });
    }

    findStaredUuidList() {
        if (this.userLogin) {
            this.ummClient.get_user_stared_uuid_list({
                'userLogin': this.userLogin,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.staredAbilityUuidList = res.body['value'];
                    }
                }
            );
        }

    }

    loadAll() {
        const queryOptions = {
            isPublic: this.isPublic,
            deployer: this.deployer,
        };
        if (this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page - 1;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.ummClient.get_deployments(queryOptions).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.totalItems = res.body['value']['total'];
                    this.abilitys = res.body['value']['results'];
                }
            }
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
            this.ummClient.delete_star_by_target_uuid({
                targetUuid: ability.uuid,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.ummClient.update_deployment_star_count({
                            deploymentId: ability.id,
                        }).subscribe();
                        ability.starCount --;
                        this.findStaredUuidList();
                    }
                }
            );
        } else {
            const star = new Star();
            star.targetType = 'AI开放能力';
            star.targetUuid = ability.uuid;

            this.ummClient.create_star({
                star,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.ummClient.update_deployment_star_count({
                            deploymentId: ability.id,
                        }).subscribe();
                        ability.starCount ++;
                        this.findStaredUuidList();
                    }
                }
            );
        }
    }

    viewAbility(ability) {
        this.router.navigate(['/ability/' + ability.uuid]);
    }

    gotoAllAbilitys() {
        this.router.navigate(['/market']);
    }

    gotoMyStars() {
        window.location.href = '/ppersonal/#/star/' + this.userLogin;
    }

    gotoDeployerStars() {
        window.location.href = '/ppersonal/#/star/' + this.deployer;
    }

    gotoPersonalModels(solutionAuthor: string) {
        window.location.href = '/pmodelhub/#/personal/' + solutionAuthor;
    }

    gotoMyTasks() {
        window.location.href = '/ppersonal/#/task';
    }

    gotoStargazers(ability) {
        if (ability.starCount > 0) {
            this.router.navigate(['/stargazer/' + ability.uuid + '/' + ability.solutionName]);
        }
    }

    gotoDemos() {
        this.router.navigate(['/demo']);
    }

    goBack() {
        this.location.back();
    }

}
