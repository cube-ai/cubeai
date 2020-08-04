import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './google-translate.component.html',
})
export class GoogleTranslateComponent implements OnInit {

    src: string;
    dest: string;
    sentence: string;
    result: string;
    sending = false;

    ngOnInit() {
        this.src = 'zh-cn';
        this.dest = 'en';
    }

    exchange() {
        const temp = this.src;
        this.src = this.dest;
        this.dest = temp;
    }

    classify() {
        const url = 'https://cubeai.dimpt.com/ability/model/20ed7fba89874b7db594b1ac8b656651/translate';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'src': this.src,
                'dest': this.dest,
                'sentence': this.sentence
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
