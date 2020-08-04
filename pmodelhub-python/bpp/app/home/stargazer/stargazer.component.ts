import {Component, OnInit} from '@angular/core';
import {Star} from '../model/star.model';
import {StarService} from '../service/star.service';
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
        private starService: StarService,
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

        this.starService.query({
            targetUuid: this.solutionUuid,
            sort: ['id,desc'],
        }).subscribe(
            (res) => {
                this.stars = res.body;
            }
        );
    }

    gotoPersonal(authorLogin: string) {
        this.router.navigate(['/personal/' + authorLogin]);
    }

}
