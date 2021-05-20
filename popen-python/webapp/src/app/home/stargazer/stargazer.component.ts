import {Component, OnInit} from '@angular/core';
import {Star} from '../model/star.model';
import {UmmClient} from '../service/umm_client.service';
import {ActivatedRoute, Router} from '@angular/router';
import {MessageService} from 'primeng/api';

@Component({
    templateUrl: './stargazer.component.html',
})
export class StargazerComponent implements OnInit {
    stars: Star[] = [];
    abilityUuid: string;
    abilityName: string;
    loading = false;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private ummClient: UmmClient,
        private messageService: MessageService,
    ) {
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.abilityUuid = params['abilityUuid'];
            this.abilityName = params['abilityName'];

            this.loadData();
        });
    }

    loadData() {
        this.loading = true;
        this.ummClient.get_stars({
            targetUuid: this.abilityUuid,
            sort: ['id,desc'],
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.loading = false;
                    this.stars = res.body['value']['results'];
                } else {
                    this.loading = false;
                    this.messageService.add({severity:'error', detail:'获取能力粉丝列表失败！'});
                }
            }, () => {
                this.loading = false;
                this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
            });
    }

    gotoPersonalAbilities(deployer: string) {
        this.router.navigate(['/personal/' + deployer]);
    }

    gotoPersonalStars(deployer: string) {
        window.location.href = '/ppersonal/#/star/' + deployer;
    }

}
