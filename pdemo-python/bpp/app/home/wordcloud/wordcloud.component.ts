import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './wordcloud.component.html',
})
export class WordcloudComponent implements OnInit {

    sentence: string;
    outputImaUrl: string;
    result: string;
    results = [];
    sending = false;
    error: boolean;

    ngOnInit() {
    }

    classify() {
        const url = 'https://cubeai.dimpt.com/ability/model/a0930e3433de4437bf28b6a6403c54f1/predict';
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
        fetch(url, params).then((data) => data.text()
        ).then((res) => {
            this.result = (JSON.parse(res)['value']);
            this.sending = false;
        }).catch((error) => {
            this.error = true;
            this.sending = false;
        });
    }

}
