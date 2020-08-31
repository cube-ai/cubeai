import {Component, OnInit} from '@angular/core';
import {Star} from '../model/star.model';
import {HttpService} from '../../shared';
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
        private http: HttpService,
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
        const body = {
            action: 'get_stars',
            args: {
                targetUuid: this.solutionUuid,
                sort: ['id,desc'],
            },
        };
        this.http.post('umm', body).subscribe(
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
