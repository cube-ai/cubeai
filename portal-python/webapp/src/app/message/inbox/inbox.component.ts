import {Component, OnInit} from '@angular/core';
import {GlobalService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {Principal, User, UaaClient} from '../../shared';
import {Message} from '../model/message.model';
import {MessageDraft} from "../model/massage-draft.model";
import {LazyLoadEvent} from 'primeng/api';
import {MessageService} from 'primeng/api';

@Component({
    templateUrl: './inbox.component.html',
    styleUrls: [
        'inbox.css',
    ]
})
export class InboxComponent implements OnInit {

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

    showWriteDlg = false;
    writeReceivers = [];
    showWriteReceivers = true;
    writeSubject: string;
    writeContent: string;
    writeUrgent = false;

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
    ) {
    }

    ngOnInit() {
        this.currentUser = this.principal.getCurrentAccount();
    }

    loadData() {
        const queryOptions = {
            receiver: this.currentUser.login,
            deleted: false,
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

    markAsDeleted(message) {
        if (!message.deleted) {
            message.deleted = true;

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

    writeMessage() {
        this.writeReceivers = [];
        this.showWriteReceivers = true;
        this.writeSubject = '';
        this.writeContent = '';
        this.writeUrgent = false;
        this.showWriteDlg = true;
    }

    sendMessage() {
        const message = new Message();
        message.subject = this.writeSubject;
        message.content = this.writeContent;
        message.urgent = this.writeUrgent;

        const messageDraft = new MessageDraft();
        messageDraft.message = message;
        messageDraft.receivers = this.writeReceivers;

        this.uaaClient.send_multicast_message({
            draft: messageDraft,
        }).subscribe((res) => {
            if (res.body['status'] === 'ok') {
                this.showWriteDlg = false;
                this.messageService.add({severity:'success', detail:'消息发送成功！'});
            } else {
                this.messageService.add({severity:'error', detail:'消息发送失败！'});
            }
        }, () => {
            this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
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
            deleted: false,
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

    checkReceiver(event) {
        const receiver = event.value;
        this.uaaClient.get_login_exist({
            login: receiver,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    if (!res.body['value']) {
                        this.writeReceivers.pop();
                        setTimeout(() => this.showWriteReceivers = false);
                        setTimeout(() => this.showWriteReceivers = true);
                        this.messageService.add({severity:'error', detail:'该收信人(' + receiver +')不存在！'});
                    }
                }
            }
        );
    }

}
