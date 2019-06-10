import { Component, Input } from '@angular/core';

/**
 * A component that will take care of item count statistics of a pagination.
 */
@Component({
    selector: 'jhi-page-item-count',
    template: `
        <div class="info" style="font-size: smaller">
            第 {{((page - 1) * itemsPerPage) == 0 ? 1 : ((page - 1) * itemsPerPage + 1)}} -
            {{(page * itemsPerPage) < total ? (page * itemsPerPage) : total}}
            条，共 {{total}} 条
        </div>`
})
export class PageItemCountComponent {

    /**
     *  current page number.
     */
    @Input() page: number;

    /**
     *  Total number of items.
     */
    @Input() total: number;

    /**
     *  Number of items per page.
     */
    @Input() itemsPerPage: number;

}
