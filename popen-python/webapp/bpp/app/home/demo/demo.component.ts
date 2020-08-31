import {Component, OnInit} from '@angular/core';
import { Location } from '@angular/common';
import {Ability} from '../model/ability.model';
import {HttpService} from '../../shared';

@Component({
    templateUrl: './demo.component.html',
})
export class DemoComponent implements OnInit {

    filter = '';
    abilitys: Ability[] = [];

    predicate = 'displayOrder';
    reverse = false;

    constructor(
        private location: Location,
        private http: HttpService,
    ) {
    }

    ngOnInit() {
        this.loadAll();
    }

    loadAll() {
        const queryOptions = {
            'isPublic': true,
            'status': '运行',
            'sort': this.sort(),
        };
        if (this.filter) {
            queryOptions['filter'] = this.filter;
        }

        const body = {
            action: 'get_deployments',
            args: queryOptions,
        };
        this.http.post('umm', body).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.abilitys = res.body['value']['results'];
                }
            }
        );
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id' + ',' + (this.reverse ? 'asc' : 'desc'));
        }
        return result;
    }

    gotoDemo(abilityUuid) {
        window.location.href = location.protocol + '//' + location.host + '/ability/web/' + abilityUuid + '/';
    }

}
