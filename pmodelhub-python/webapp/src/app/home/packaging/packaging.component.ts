import {Component, OnInit} from '@angular/core';
import {GlobalService, UaaClient} from '../../shared';
import {Location} from '@angular/common';
import {Router} from '@angular/router';

@Component({
    templateUrl: './packaging.component.html',
})
export class PackagingComponent implements OnInit {
    content: string;

    constructor(
        private location: Location,
        private router: Router,
        private globalService: GlobalService,
        private uaaClient: UaaClient,
    ) {
    }

    ngOnInit() {
        this.uaaClient.get_articles({
            subject1: 'model-packaging',
        }).subscribe((res) => {
            if (res.body['status'] === 'ok' && res.body['value']['total'] > 0) {
                this.content = res.body['value']['results'][0].content;
            }
        });
    }

}
