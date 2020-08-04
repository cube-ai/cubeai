import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './pinyin.component.html',
})
export class PinyinComponent implements OnInit {

    hanzi: string;
    split = ' ';
    style = '1';
    multi = false;
    pinyin: string;
    sending = false;

    ngOnInit() {
    }

    han2pin() {
        const url = 'https://cubeai.dimpt.com/ability/model/5c5d7c8ce1704bc0a12dd3a3be558d8d/pinyin';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'hanzi': this.hanzi,
                'split': this.split,
                'style': this.style,
                'multi': this.multi ? 1 : 0,
            }),
            method: 'POST',
        };

        this.sending = true;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.pinyin = JSON.parse(res)['value'];
                this.sending = false;
            })
            .catch((error) => {
                this.pinyin = '出错啦...';
                this.sending = false;
            });
    }

}
