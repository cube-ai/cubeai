import {Component, OnInit, ViewChild} from '@angular/core';
import {ConfirmService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService, GlobalService} from '../../shared';
import {AttachmentService} from './attachment.service';
import {Attachment} from './attachment.model';
import {Router} from '@angular/router';
import {Principal, User} from '../../account';
import {MatPaginator, PageEvent} from '@angular/material';
import {FileUploader} from 'ng2-file-upload';
import {SERVER_API_URL} from '../../app.constants';
import {CookieService} from 'ngx-cookie';
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
        private globalService: GlobalService,
        private router: Router,
        private principal: Principal,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
        private attachmentService: AttachmentService,
        private cookieService: CookieService,
    ) {
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }

        this.user = this.principal.getCurrentAccount();
        this.loadAll();

        this.uploader = new FileUploader({
            url: SERVER_API_URL + 'zuul/uaa/api/attachments/upload',
            method: 'POST',
            itemAlias: 'attachment',
        });
        this.uploader.onBeforeUploadItem = (fileItem) => {
            fileItem.headers.push({name: 'X-XSRF-TOKEN', value: this.cookieService.get('XSRF-TOKEN')});
            return fileItem;
        };
    }

    loadAll() {
        this.attachmentService.query({
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort(),
        }).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
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

    private onSuccess(data, headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.attachments = data;
    }

    private onError(error) {
    }

    upload(fileItem: FileItem) {
        fileItem.onSuccess = () => {
            this.refresh();
        };

        fileItem.onError = () => {
            this.refresh();
        };

        if (fileItem.file.size > 100 * 1024 * 1024) {
            this.snackBarService.error('上传文档不能大于100MB！');
            fileItem.remove();
            return;
        }

        fileItem.upload();

    }

    deleteAttachment(attachment: Attachment) {
        this.confirmService.ask('确定要删除该附件？').then((confirm) => {
            if (confirm) {
                this.attachmentService.delete(attachment.id).subscribe(
                    () => {
                        this.refresh();
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
