import {Component, OnInit} from '@angular/core';
import {GlobalService, UaaClient, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {Principal, User} from '../../shared';
import {Message} from '../model/message.model';
import {LazyLoadEvent} from 'primeng/api';
import {MessageService} from 'primeng/api';
import {ConfirmationService} from 'primeng/api';

@Component({
    templateUrl: './deleted.component.html',
    styleUrls: [
        'deleted.css',
    ]
})
export class DeletedComponent implements OnInit {

    currentUser: User;
    messages: Message[] = [];
    unreadCount = 0;

    filter = '';
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 0;
    predicate = 'id';
    reverse = false;
    loading = false;

    showViewDlg = false;
    viewSender: string;
    viewSubject: string;
    viewContent: string;
    viewUrgent = false;
    viewDate: Date;
    viewUrl:string;

    constructor(
        public globalService: GlobalService,
        private principal: Principal,
        private uaaClient: UaaClient,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
    ) {
    }

    ngOnInit() {
        this.currentUser = this.principal.getCurrentAccount();
    }

    loadData() {
        const queryOptions = {
            receiver: this.currentUser.login,
            deleted: true,
        };
        if (this.filter) {
            queryOptions['filter'] = this.filter;
        }
        queryOptions['page'] = this.page;
        queryOptions['size'] = this.itemsPerPage;
        queryOptions['sort'] = this.sort();

        this.loading = true;
        this.uaaClient.get_messages(queryOptions).subscribe(
            (res) => {
                this.loading = false;
                if (res.body['status'] === 'ok') {
                    this.totalItems = res.body['value']['total'];
                    this.messages = res.body['value']['results'];
                    this.getUnreadCount();
                } else {
                    this.messageService.add({severity:'error', detail:'获取消息失败！'});
                }
            }, () => {
                this.loading = false;
                this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
            });
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
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

    markAsViewed(message) {
        if (!message.viewed) {
            message.viewed = true;

            this.uaaClient.mark_message_viewed({
                id: message.id,
                viewed: message.viewed,
            }).subscribe(
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

            this.uaaClient.mark_message_viewed({
                id: message.id,
                viewed: message.viewed,
            }).subscribe(
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

            this.uaaClient.mark_message_deleted({
                id: message.id,
                deleted: message.deleted,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.loadData();
                    }
                }
            );
        }
    }

    deleteMessage(message) {
        this.confirmationService.confirm({
            target: event.target,
            message: '删除后不能恢复！确定要删除该消息？',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: '是',
            rejectLabel: '否',
            accept: () => {
                this.uaaClient.delete_message({
                    id: message.id,
                }).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.loadData();
                        }
                    }
                );
            },
            reject: () => {}
        });
    }

    viewMessage(message: Message) {
        this.viewSender = message.sender;
        this.viewSubject = message.subject;
        this.viewContent = message.content;
        this.viewUrgent = message.urgent;
        this.viewDate = message.createdDate;
        this.viewUrl = message.url;
        this.markAsViewed(message);
        this.showViewDlg = true;
    }

    gotoUrl() {
        this.showViewDlg = false;
        window.location.href = this.viewUrl;
    }

    getUnreadCount() {
        this.uaaClient.get_unread_message_count({
            receiver: this.currentUser.login,
            deleted: true,
        }).subscribe(
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
