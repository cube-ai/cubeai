import {Component, OnInit, Inject, ViewChild} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA, MatPaginator, PageEvent} from '@angular/material';
import {PublishRequestService} from '../';
import {PublishRequest} from '../model/publish-request.model';
import {ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService} from '../../shared';

@Component({
    templateUrl: './approve-history.component.html',
    styleUrls: ['../ucumos-datapage.css'],
})
export class ApproveHistoryComponent implements OnInit {

    publishRequests: PublishRequest[] = [];

    @ViewChild(MatPaginator) paginator: MatPaginator;
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = 5;
    previousItemsPerPage = 5;
    totalItems: number;
    page = 1;
    previousPage = 1;
    predicate = 'id';
    reverse = false;

    constructor(
        public dialogRef: MatDialogRef<ApproveHistoryComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private publishRequestService: PublishRequestService,
        private snackBarService: SnackBarService,
    ) {
    }

    ngOnInit() {
        this.loadAll();
    }

    loadAll() {
        this.publishRequestService.query({
            solutionUuid: this.data.solutionUuid,
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort()
        }).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
        );
    }

    private onSuccess(data, headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.publishRequests = data;
    }

    private onError(error) {
    }

    trackIdentity(index, item: PublishRequest) {
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

    onClose(): void {
        this.dialogRef.close();
    }

}
