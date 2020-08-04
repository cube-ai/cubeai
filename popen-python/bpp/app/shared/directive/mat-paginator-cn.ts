import {MatPaginatorIntl} from '@angular/material';
import {Injectable} from '@angular/core';

@Injectable()
export class MatPaginatorCn extends MatPaginatorIntl {
    firstPageLabel = '第一页';
    lastPageLabel = '最后一页';
    nextPageLabel = '下一页';
    previousPageLabel = '上一页';
    itemsPerPageLabel = '每页条数：';
    getRangeLabel = (page: number, pageSize: number, length: number) => {
        if (length === 0 || pageSize === 0) {
            return '无数据';
        }

        length = Math.max(length, 0);
        const startIndex = page * pageSize;
        const endIndex = startIndex < length ? Math.min(startIndex + pageSize, length) : startIndex + pageSize;
        const pageNum = Math.ceil(length / pageSize);
        return `第 ${page + 1} 页，共 ${pageNum} 页； 第 ${startIndex + 1} - ${endIndex} 条，共 ${length} 条`;
    }

    constructor() {
        super();
    }

}
