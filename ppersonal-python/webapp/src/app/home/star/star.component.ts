import {Component, OnInit} from '@angular/core';
import { Subscription } from 'rxjs';
import {ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {Principal} from '../../shared';
import {UmmClient} from '../service/umm_client.service';
import {Location} from '@angular/common';
import {Star} from '../model/star.model';
import {ActivatedRoute, Router} from '@angular/router';
import { LazyLoadEvent } from 'primeng/api';
import {MessageService} from 'primeng/api';

@Component({
    templateUrl: './star.component.html',
})
export class StarComponent implements OnInit {
    subscription: Subscription;
    userLogin = '';  // 本人
    starerLogin = '';  // 当前查看的其他收藏者
    isMyself: boolean;
    stars: Star[];

    filter = '';
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 0;
    predicate = 'id';
    reverse = false;
    loading = false;
    layout = 'list';
    pagePrefix = 'mystar';

    constructor(
        private principal: Principal,
        private location: Location,
        private route: ActivatedRoute,
        private router: Router,
        private ummClient: UmmClient,
        private messageService: MessageService,
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.starerLogin = params['starerLogin'];
            if (this.starerLogin === 'null') {
                this.goBack();
            }
            this.pagePrefix = 'mystar_' + this.starerLogin;
            this.restorePageState();

            this.principal.updateCurrentAccount().then(() => {
                this.userLogin = this.principal.getLogin();
                this.isMyself = this.userLogin === this.starerLogin;
            });
        });
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
        const queryOptions = {
            userLogin: this.starerLogin,
        };
        if (!!this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.loading = true;
        this.ummClient.get_stars(queryOptions).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.loading = false;
                    this.totalItems = res.body['value']['total'];
                    this.stars = res.body['value']['results'];

                    this.stars.forEach((star) => {
                        this.ummClient.get_solutions({
                            uuid: star.targetUuid,
                        }).subscribe((res1) => {
                            if (res1.body['status'] === 'ok' && res1.body['value']['total'] > 0) {
                                star.targetObject = res1.body['value']['results'][0];
                            } else {
                                star.targetObject = null;
                                this.ummClient.delete_star({starId: star.id}).subscribe();
                            }
                        });
                    });
                } else {
                    this.loading = false;
                    this.messageService.add({severity:'error', detail:'获取关注列表失败！'});
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

        this.backupPageState();
        this.loadData();
    }

    changeLayout(event) {
        this.layout = event.layout;
        this.backupPageState();
    }

    viewStar(star: Star) {
        window.location.href = '/pmodelhub/#/solution/' + star.targetUuid;
    }

    goBack() {
        this.location.back();
    }

}
