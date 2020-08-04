import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './dimsim.component.html',
})
export class DimsimComponent implements OnInit {

    sentence: string;
    similarity: string;
    sentence1: string;
    sentence2: string;
    candidates = [];
    sending = false;

    ngOnInit() {
    }

    get_distance() {
        const url = 'https://cubeai.dimpt.com/ability/model/e6f7719ec11440edbb0c35a4eb619455/get_distance';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'sentence1': this.sentence1,
                'sentence2': this.sentence2,
            }),
            method: 'POST',
        };

        this.sending = true;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.similarity = JSON.parse(res)['value'] + '';
                this.sending = false;
            })
            .catch((error) => {
                this.similarity = '出错啦...';
                this.sending = false;
            });
    }

    get_candidates() {
        const url = 'https://cubeai.dimpt.com/ability/model/e6f7719ec11440edbb0c35a4eb619455/get_candidates';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'sentence': this.sentence,
            }),
            method: 'POST',
        };

        this.sending = true;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.candidates = JSON.parse(res)['value'];
                this.sending = false;
            })
            .catch((error) => {
                this.candidates = [];
                this.candidates.push('出错啦...');
                this.sending = false;
            });
    }

}
