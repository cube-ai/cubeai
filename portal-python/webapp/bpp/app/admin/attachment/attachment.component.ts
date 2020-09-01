import {Component, OnInit, ViewChild} from '@angular/core';
import {ConfirmService, SnackBarService, GlobalService, UaaClient, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {Attachment} from './attachment.model';
import {Router} from '@angular/router';
import {Principal, User} from '../../shared';
import {MatPaginator, PageEvent} from '@angular/material';
import {FileUploader} from 'ng2-file-upload';
import {SERVER_API_URL} from '../../app.constants';
import {FileItem} from 'ng2-file-upload/file-upload/file-item.class';

@Component({
    templateUrl: './attachment.component.html',
    styleUrls: [
        '../admin-datapage.css'
    ]
})
export class AttachmentComponent implements OnInit {
    user: User;
    attachments: Attachment[] = [];
    uploader: FileUploader;

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
        private router: Router,
        private principal: Principal,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
        private uaaClient: UaaClient,
    ) {
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }

        this.user = this.principal.getCurrentAccount();
        this.loadAll();

        this.uploader = new FileUploader({
            url: SERVER_API_URL + 'uaa/api/file/upload_attachment',
            method: 'POST',
            itemAlias: 'upload_attachment',
        });
    }

    loadAll() {
        this.uaaClient.get_attachments({
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort(),
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.totalItems = res.body['value']['total'];
                    this.attachments = res.body['value']['results'];
                }
            }
        );
    }

    trackIdentity(index, item: Attachment) {
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

    upload(fileItem: FileItem) {
        fileItem.onSuccess = () => {
            this.refresh();
        };

        fileItem.onError = () => {
            this.refresh();
        };

        if (fileItem.file.size > 800 * 1024 * 1024) {
            this.snackBarService.error('上传文档不能大于800MB！');
            fileItem.remove();
            return;
        }

        fileItem.upload();

    }

    deleteAttachment(attachment: Attachment) {
        this.confirmService.ask('确定要删除该附件？').then((confirm) => {
            if (confirm) {
                this.uaaClient.delete_attachment({
                    id: attachment.id,
                }).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.refresh();
                        } else {
                            this.snackBarService.error('删除失败：' + res.body['value']);
                        }
                    }, () => {
                        this.snackBarService.error('删除失败！');
                    }
                );
            }
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
