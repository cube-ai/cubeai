import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './idiom.component.html',
})
export class IdiomComponent implements OnInit {

    idiom: string;
    next: string;
    history: string[];
    error: boolean;
    sending = false;

    ngOnInit() {
        this.history = [];
    }

    predict() {
        const url = 'https://cubeai.dimpt.com/ability/model/fb0f6acde4744a258fd409a9ecb0ba23/predict';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'idiom': this.idiom,
            }),
            method: 'POST',
        };

        this.sending = true;
        this.error = false;
        if (this.idiom !== this.next) {
            this.history = [];
        }
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                const result = JSON.parse(JSON.parse(res)['value']);
                this.history.unshift(this.idiom);
                this.next = result['idiom'];
                this.idiom = result['idiom'];
                this.sending = false;
            })
            .catch((error) => {
                this.error = true;
                this.sending = false;
            });
    }

}
