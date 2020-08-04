import {Component, OnInit} from '@angular/core';
import {Star} from '../model/star.model';
import {StarService} from '../service/star.service';
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
        private starService: StarService,
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

        this.starService.query({
            targetUuid: this.abilityUuid,
            sort: ['id,desc'],
        }).subscribe(
            (res) => {
                this.stars = res.body;
            }
        );
    }

    gotoPersonal(deployer: string) {
        this.router.navigate(['/personal/' + deployer]);
    }

}
