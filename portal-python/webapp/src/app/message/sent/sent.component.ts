import {Component, OnInit} from '@angular/core';
import {GlobalService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {Principal, User, UaaClient} from '../../shared';
import {Message} from '../model/message.model';
import {LazyLoadEvent} from 'primeng/api';
import {MessageService} from 'primeng/api';

@Component({
    templateUrl: './sent.component.html',

})
export class SentComponent implements OnInit {

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
    viewReceiver: string;
    viewSubject: string;
    viewContent: string;
    viewUrgent = false;
    viewDate: Date;

    constructor(
        public globalService: GlobalService,
        private uaaClient: UaaClient,
        private principal: Principal,
        private messageService: MessageService,
    ) {
    }

    ngOnInit() {
        this.currentUser = this.principal.getCurrentAccount();
    }

    loadData() {
        const queryOptions = {
            sender: this.currentUser.login,
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

    viewMessage(message: Message) {
        this.viewReceiver = message.receiver;
        this.viewSubject = message.subject;
        this.viewContent = message.content;
        this.viewUrgent = message.urgent;
        this.viewDate = message.createdDate;
        this.showViewDlg = true;
    }

    abbreviateText(text: string, num: number): string {
        if (text.length < num + 1) {
            return text;
        } else {
            return text.substring(0, num) + '...';
        }
    }

}
