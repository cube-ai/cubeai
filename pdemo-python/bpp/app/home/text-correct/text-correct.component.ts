import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './text-correct.component.html',
})
export class TextCorrectComponent implements OnInit {

    text: string;
    result: string;
    sending = false;

    ngOnInit() {
    }

    correct() {
        const url = 'https://cubeai.dimpt.com/ability/model/29f642ce792541f3a5c96a26b107888e/correct';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'text': this.text
            }),
            method: 'POST',
        };

        this.sending = true;
        this.result = '';
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.result = JSON.parse(JSON.parse(res)['value'])['corrected'];
                this.sending = false;
            })
            .catch((error) => {
                this.result = '出错啦...';
                this.sending = false;
            });
    }

}
