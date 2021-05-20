import {Component, OnInit} from '@angular/core';
import {GlobalService, UaaClient, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {Attachment} from './attachment.model';
import {Router} from '@angular/router';
import {Principal, User} from '../../shared';
import {FileUploader} from 'ng2-file-upload';
import {SERVER_API_URL} from '../../app.constants';
import {FileItem} from 'ng2-file-upload/file-upload/file-item.class';
import { LazyLoadEvent } from 'primeng/api';
import {MessageService} from 'primeng/api';
import {ConfirmationService} from 'primeng/api';

@Component({
    templateUrl: './attachment.component.html',
})
export class AttachmentComponent implements OnInit {
    user: User;
    attachments: Attachment[] = [];
    uploader: FileUploader;

    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 0;
    predicate = 'id';
    reverse = false;
    loading = false;

    constructor(
        public globalService: GlobalService,
        private router: Router,
        private principal: Principal,
        private uaaClient: UaaClient,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
    ) {
    }

    ngOnInit() {
        this.user = this.principal.getCurrentAccount();
        this.uploader = new FileUploader({
            url: SERVER_API_URL + 'uaa/api/file/upload_attachment',
            method: 'POST',
            itemAlias: 'upload_attachment',
        });
    }

    loadData() {
        this.loading = true;
        this.uaaClient.get_attachments({
            page: this.page,
            size: this.itemsPerPage,
            sort: this.sort(),
        }).subscribe(
            (res) => {
                this.loading = false;
                if (res.body['status'] === 'ok') {
                    this.totalItems = res.body['value']['total'];
                    this.attachments = res.body['value']['results'];
                } else {
                    this.messageService.add({severity:'error', detail:'获取附件列表失败！'});
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

        this.loadData();
    }

    upload(fileItem: FileItem) {
        fileItem.onSuccess = () => {
            this.loadData();
        };

        fileItem.onError = () => {
            this.loadData();
        };

        if (fileItem.file.size > 800 * 1024 * 1024) {
            this.messageService.add({severity:'error', detail:'上传文档不能大于800MB！'});
            fileItem.remove();
            return;
        }

        fileItem.upload();

    }

    deleteAttachment(attachment: Attachment) {
        this.confirmationService.confirm({
            target: event.target,
            message: '确定要删除该附件？',
            icon: 'pi pi-exclamation-triangle',
            accept: () => {
                this.uaaClient.delete_attachment({
                    id: attachment.id,
                }).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.loadData();
                        } else {
                            this.messageService.add({severity:'error', detail:'删除失败！'});
                        }
                    }, () => {
                        this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
                    }
                );
            },
            reject: () => {}
        });
    }

    downloadFile(url: string) {
        const link: HTMLElement = document.createElement('a');
        link.setAttribute('href', url);
        link.setAttribute('download', '');
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

}
