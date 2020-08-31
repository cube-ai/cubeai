import {Component, OnInit, ViewChild} from '@angular/core';
import {ConfirmService, GlobalService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService} from '../../shared';
import {Principal, User, HttpService} from '../../shared';
import {Message} from '../model/message.model';
import {MessageEditComponent} from '../';
import {MatDialog, MatPaginator, PageEvent} from '@angular/material';

@Component({
    templateUrl: './sent.component.html',
    styleUrls: [
        '../message-datapage.css'
    ]
})
export class SentComponent implements OnInit {

    filter = '';

    currentUser: User;
    messages: Message[] = [];
    unreadCount = 0;

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
        public globalService: GlobalService,
        private dialog: MatDialog,
        private http: HttpService,
        private principal: Principal,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
    ) {
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }
        this.currentUser = this.principal.getCurrentAccount();
        this.loadAll();
    }

    loadAll() {
        const queryOptions = {
            sender: this.currentUser.login,
        };
        if (this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page - 1;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        const body = {
            action: 'get_messages',
            args: queryOptions,
        };
        this.http.post('uaa', body).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.totalItems = res.body['value']['total'];
                    this.messages = res.body['value']['results'];
                } else {
                    this.snackBarService.error('获取消息出错！');
                }
            }, (res) => {
                this.snackBarService.error('获取消息出错！');
            }
        );
    }

    trackIdentity(index, item: Message) {
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

    viewMessage(message) {
        const dialogRef = this.dialog.open(MessageEditComponent, {
            width: '800px',
            data: {
                operation: 'view',
                viewSent: true,
                message,
            },
        });
    }

    abbreviateText(text: string, num: number): string {
        if (text.length < num + 1) {
            return text;
        } else {
            return text.substring(0, num) + '...';
        }
    }

}
