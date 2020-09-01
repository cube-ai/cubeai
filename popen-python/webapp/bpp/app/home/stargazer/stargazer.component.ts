import {Component, OnInit} from '@angular/core';
import {Star} from '../model/star.model';
import {UmmClient} from '../';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    templateUrl: './stargazer.component.html',
})
export class StargazerComponent implements OnInit {

    stars: Star[] = [];
    abilityUuid: string;
    abilityName: string;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private ummClient: UmmClient,
    ) {
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.abilityUuid = params['abilityUuid'];
            this.abilityName = params['abilityName'];
        });
        this.loadAll();
    }

    loadAll() {
        this.ummClient.get_stars({
            targetUuid: this.abilityUuid,
            sort: ['id,desc'],
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.stars = res.body['value']['results'];
                }
            }
        );
    }

    gotoPersonal(deployer: string) {
        this.router.navigate(['/personal/' + deployer]);
    }

}
