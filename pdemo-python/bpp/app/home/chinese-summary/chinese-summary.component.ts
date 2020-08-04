import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './chinese-summary.component.html',
})
export class ChineseSummaryComponent implements OnInit {

    sentence: string;
    result: string;
    sending = false;

    ngOnInit() {
    }

    classify() {
        const url = 'https://cubeai.dimpt.com/ability/model/7f163882b2e1467c8e7a07da4a641c18/predict';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'text': this.sentence
            }),
            method: 'POST',
        };

        this.sending = true;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.result = JSON.parse(res)['value'];
                this.sending = false;
            })
            .catch((error) => {
                this.result = '短时间内查询次数太多，请一个小时之后再试...';
                this.sending = false;
            });
    }

}
