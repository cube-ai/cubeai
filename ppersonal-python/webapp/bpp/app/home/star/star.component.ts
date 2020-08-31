import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, PageEvent} from '@angular/material';
import { Subscription } from 'rxjs/Subscription';
import {ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {Principal, HttpService} from '../../shared';
import {Location} from '@angular/common';
import {Star} from '../model/star.model';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    templateUrl: './star.component.html',
})
export class StarComponent implements OnInit {
    isMobile = window.screen.width < 960;

    subscription: Subscription;
    userLogin = '';  // 本人
    starerLogin = '';  // 当前查看的其他收藏者
    isMyself: boolean;
    stars: Star[];

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
        private principal: Principal,
        private location: Location,
        private route: ActivatedRoute,
        private router: Router,
        private http: HttpService,
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.starerLogin = params['starerLogin'];
            if (this.starerLogin === 'null') {
                this.goBack();
            }
        });

        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.isMyself = this.userLogin === this.starerLogin;
            this.loadAll();
        });
    }

    loadAll() {
        const queryOptions = {
            userLogin: this.starerLogin,
        };
        queryOptions['page'] = this.page - 1;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        const body = {
            action: 'get_stars',
            args: queryOptions,
        };
        this.http.post('umm', body).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.totalItems = res.body['value']['total'];
                    this.stars = res.body['value']['results'];

                    this.stars.forEach((star) => {
                        if (star.targetType === 'AI模型') {
                            const body1 = {
                                action: 'get_solutions',
                                args: {
                                    uuid: star.targetUuid,
                                },
                            };
                            this.http.post('umm', body1).subscribe((res1) => {
                                if (res1.body['status'] === 'ok' && res1.body['value']['total'] > 0) {
                                    star.targetObject = res1.body['value']['results'][0];
                                } else {
                                    star.targetObject = null;
                                }
                            });
                        } else if (star.targetType === 'AI开放能力') {
                            const body2 = {
                                action: 'get_deployments',
                                args: {
                                    uuid: star.targetUuid,
                                },
                            };
                            this.http.post('umm', body2).subscribe((res2) => {
                                if (res2.body['status'] === 'ok' && res2.body['value']['total'] > 0) {
                                    star.targetObject = res2.body['value']['results'][0];
                                } else {
                                    star.targetObject = null;
                                }
                            });
                        }
                    });
                }
            }
        );
    }

    trackIdentity(index, item: Star) {
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

    viewStar(star: Star) {
        if (star.targetType === 'AI模型') {
            window.location.href = '/pmodelhub/#/solution/' + star.targetUuid;
        } else if (star.targetType === 'AI开放能力') {
            window.location.href = '/popen/#/ability/' + star.targetUuid;
        }
    }

    goBack() {
        this.location.back();
    }

}
