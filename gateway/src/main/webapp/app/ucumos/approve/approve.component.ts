import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, PageEvent} from '@angular/material';
import {ConfirmService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService} from '../../shared';
import {PublishRequestService} from '../service/publish-request.service';
import {PublishRequest} from '../model/publish-request.model';
import {Router} from '@angular/router';

@Component({
    templateUrl: './approve.component.html',
    styleUrls: [
        '../ucumos-datapage.css'
    ]
})
export class ApproveComponent implements OnInit {

    reviewTab = '等待审批';

    publishRequests: PublishRequest[] = [];

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
        private router: Router,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
        private publishRequestService: PublishRequestService,
    ) {
    }

    ngOnInit() {
        this.loadAll();
    }

    loadAll() {
        if (this.reviewTab === '等待审批') {
            this.loadWaitReview();
        } else if (this.reviewTab === '已审批') {
            this.loadReviewed();
        }
    }

    loadWaitReview() {
        this.publishRequestService.query({
            reviewed: false,
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort()
        }).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
        );
    }

    loadReviewed() {
        this.publishRequestService.query({
            reviewed: true,
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort(),
        }).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
        );
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

    private onSuccess(data, headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.publishRequests = data;
        // TODO: 如因删除等原因致使当前页超出页面总数，此时MatPaginator不会自动跳转至最后一页，而是显示空白页。暂时维持该bug待以后修正。
    }

    private onError(error) {
    }

    reviewRequest(publishRequest: PublishRequest) {
        if (publishRequest.requestType === '申请上架') {
            this.router.navigate(['/ucumos/solution/' + publishRequest.solutionUuid + '/' + 'reviewpublish' + '/' + publishRequest.id]);
        }
        if (publishRequest.requestType === '申请下架') {
            this.router.navigate(['/ucumos/solution/' + publishRequest.solutionUuid + '/' + 'reviewunpublish' + '/' + publishRequest.id]);
        }
    }

    viewSolution(solutionUuid: string) {
        this.router.navigate(['/ucumos/solution/' + solutionUuid + '/' + 'view']);
    }

}
