import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './couplet.component.html',
})
export class CoupletComponent implements OnInit {

    shanglian: string;
    xialian: string;
    couplet: string;
    error: boolean;
    sending = false;

    ngOnInit() {
    }

    predict() {
        const url = 'https://cubeai.dimpt.com/ability/model/af1db9ce5c72455b9d9cc39363cad031/predict';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'shanglian': this.shanglian,
            }),
            method: 'POST',
        };

        this.sending = true;
        this.error = false;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.xialian = JSON.parse(res)['value'];
                this.sending = false;
            })
            .catch((error) => {
                this.error = true;
                this.sending = false;
            });
    }

    genShanglian() {
        const url = 'https://cubeai.dimpt.com/ability/model/af1db9ce5c72455b9d9cc39363cad031/gen_shanglian';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'text': '',
            }),
            method: 'POST',
        };

        this.sending = true;
        this.error = false;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.shanglian = JSON.parse(res)['value'];
                this.xialian = null;
                this.couplet = null;
                this.sending = false;
            })
            .catch((error) => {
                this.error = true;
                this.sending = false;
            });
    }

    genCouplet() {
        const url = 'https://cubeai.dimpt.com/ability/model/af1db9ce5c72455b9d9cc39363cad031/gen_couplet';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'text': '',
            }),
            method: 'POST',
        };

        this.sending = true;
        this.error = false;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.couplet = JSON.parse(res)['value'];
                const duilian = this.couplet.split('    ');
                this.shanglian = duilian[0];
                this.xialian = null;
                this.sending = false;
            })
            .catch((error) => {
                this.error = true;
                this.sending = false;
            });
    }

}
