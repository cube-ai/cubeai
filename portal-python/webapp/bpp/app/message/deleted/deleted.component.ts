import {Component, OnInit, ViewChild} from '@angular/core';
import {MatDialog, MatPaginator, PageEvent} from '@angular/material';
import {ConfirmService, GlobalService, SnackBarService, HttpService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {Principal, User} from '../../shared';
import {Message} from '../model/message.model';
import {MessageEditComponent} from '../';

@Component({
    templateUrl: './deleted.component.html',
    styleUrls: [
        'deleted.css',
        '../message-datapage.css'
    ]
})
export class DeletedComponent implements OnInit {

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
        private principal: Principal,
        private http: HttpService,
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
            receiver: this.currentUser.login,
            deleted: true,
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
                    this.getUnreadCount();
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

    markAsViewed(message) {
        if (!message.viewed) {
            message.viewed = true;

            const body = {
                action: 'mark_message_viewed',
                args: {
                    id: message.id,
                    viewed: message.viewed,
                },
            };
            this.http.post('uaa', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.unreadCount --;
                    }
                }
            );
        }
    }

    markAsUnviewed(message) {
        if (message.viewed) {
            message.viewed = false;

            const body = {
                action: 'mark_message_viewed',
                args: {
                    id: message.id,
                    viewed: message.viewed,
                },
            };
            this.http.post('uaa', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.unreadCount ++;
                    }
                }
            );
        }
    }

    markAsUndeleted(message) {
        if (message.deleted) {
            message.deleted = false;

            const body = {
                action: 'mark_message_deleted',
                args: {
                    id: message.id,
                    deleted: message.deleted,
                },
            };
            this.http.post('uaa', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.refresh();
                    }
                }
            );
        }
    }

    deleteMessage(message) {
        const body = {
            action: 'delete_message',
            args: {
                id: message.id,
            },
        };
        this.http.post('uaa', body).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.refresh();
                }
            }
        );
    }

    viewMessage(message) {
        this.markAsViewed(message);
        const dialogRef = this.dialog.open(MessageEditComponent, {
            width: '800px',
            data: {
                operation: 'view',
                message,
            },
        });
    }

    getUnreadCount() {
        const body = {
            action: 'get_unread_message_count',
            args: {
                receiver: this.currentUser.login,
                deleted: true,
            },
        };
        this.http.post('uaa', body).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.unreadCount = res.body['value'];
                }
            }
        );
    }

    abbreviateText(text: string, num: number): string {
        if (text.length < num + 1) {
            return text;
        } else {
            return text.substring(0, num) + '...';
        }
    }

}
