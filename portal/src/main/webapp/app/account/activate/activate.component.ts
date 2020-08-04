import { Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { ActivateService } from '../service/activate.service';

@Component({
    templateUrl: './activate.component.html'
})
export class ActivateComponent implements OnInit {

    status = '';
    activateKey: string;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private activateService: ActivateService,
    ) {
    }

    ngOnInit() {
        this.route.params.subscribe((params) => {
            this.activateKey = params['activateKey'];
            this.activate();
        });
    }

    activate() {
        this.activateService.get(this.activateKey).subscribe(
            () => {
                this.status = 'success';
            }, () => {
                this.status = 'fail';
            });
    }

    goLogin() {
        this.router.navigate(['/login']);
    }

}
