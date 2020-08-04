import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './sentiment.component.html',
})
export class SentimentComponent implements OnInit {

    text: string;
    sentiment_score: number;
    label: string;
    sending = false;

    ngOnInit() {
    }

    classify() {
        // const url = location.protocol + '//' + location.host + '/ability/model/a0c6185c0fa2486db5ec446b603c6439/classify'; // 用于生产环境正式部署
        const url = 'https://cubeai.dimpt.com/ability/model/a0c6185c0fa2486db5ec446b603c6439/classify'; // 用于开发环境连接生产环境测试

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
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.sentiment_score = JSON.parse(res)['value']['sentence sentiment'];
                this.label = (this.sentiment_score > 0.5 ? '正向' : '负向') + '，' + this.sentiment_score;
                this.sending = false;
            })
            .catch((error) => {
                this.label = '出错啦...';
                this.sending = false;
            });
    }

}
