import {Component, OnInit} from '@angular/core';
import {Star} from '../model/star.model';
import {UmmClient} from '../';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    templateUrl: './stargazer.component.html',
})
export class StargazerComponent implements OnInit {

    stars: Star[] = [];
    solutionUuid: string;
    solutionName: string;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private ummClient: UmmClient,
    ) {
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.solutionUuid = params['solutionUuid'];
            this.solutionName = params['solutionName'];
        });
        this.loadAll();
    }

    loadAll() {
        this.ummClient.get_stars({
            targetUuid: this.solutionUuid,
            sort: ['id,desc'],
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.stars = res.body['value']['results'];
                }
            }
        );
    }

    gotoPersonal(authorLogin: string) {
        this.router.navigate(['/personal/' + authorLogin]);
    }

}
