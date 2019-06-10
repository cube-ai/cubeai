import {Component, OnInit, ViewChild} from '@angular/core';
import {MatDialog, MatPaginator, PageEvent} from '@angular/material';
import {ConfirmService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService} from '../../shared';
import {Principal, User} from '../../account';
import {Message} from '../model/message.model';
import {MessageService} from '../service/message.service';
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
        private dialog: MatDialog,
        private principal: Principal,
        private messageService: MessageService,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
    ) {
    }

    ngOnInit() {
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

        this.messageService.query(queryOptions).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
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

    private onSuccess(data, headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.messages = data;
        this.getUnreadCount();
    }

    private onError(error) {
        this.snackBarService.error('获取消息出错！');
    }

    markAsViewed(message) {
        if (!message.viewed) {
            message.viewed = true;

            this.messageService.updateMessageViewed(message.id, message.viewed).subscribe(
                () => {
                    this.unreadCount --;
                }
            );
        }
    }

    markAsUnviewed(message) {
        if (message.viewed) {
            message.viewed = false;

            this.messageService.updateMessageViewed(message.id, message.viewed).subscribe(
                () => {
                    this.unreadCount ++;
                }
            );
        }
    }

    markAsUndeleted(message) {
        if (message.deleted) {
            message.deleted = false;

            this.messageService.updateMessageDeleted(message.id, message.deleted).subscribe(
                () => this.refresh()
            );
        }
    }

    deleteMessage(message) {
        this.messageService.delete(message.id).subscribe(
            () => this.refresh()
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
        this.messageService.getUnreadCount({
            receiver: this.currentUser.login,
            deleted: true,
        }).subscribe(
            (res) => {
                this.unreadCount = res.body;
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
