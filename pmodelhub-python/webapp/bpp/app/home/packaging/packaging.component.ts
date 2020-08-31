import {Component, OnInit} from '@angular/core';
import {GlobalService, HttpService} from '../../shared';
import {Location} from '@angular/common';
import {Router} from '@angular/router';

@Component({
    templateUrl: './packaging.component.html',
})
export class PackagingComponent implements OnInit {
    content = 'CubeAI模型打包指南';

    constructor(
        private location: Location,
        private router: Router,
        private globalService: GlobalService,
        private http: HttpService,
    ) {
    }

    ngOnInit() {
        const body = {
            action: 'get_articles',
            args: {
                subject1: 'model-packaging',
            },
        };
        this.http.post('uaa', body).subscribe((res) => {
            if (res.body['status'] === 'ok' && res.body['value']['total'] > 0) {
                this.content = res.body['value']['results'][0].content;
            }
        });
    }

}
