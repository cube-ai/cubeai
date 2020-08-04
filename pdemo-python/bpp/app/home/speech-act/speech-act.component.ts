import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './speech-act.component.html',
})
export class SpeechActComponent implements OnInit {

    text: string;
    label: string;
    score: string;
    sending = false;

    ngOnInit() {
    }

    predict() {
        // const url = location.protocol + '//' + location.host + '/ability/model/b163b9e6b8294ef2bca08b67455c38ce/predict'; // 用于生产环境正式部署
        const url = 'https://cubeai.dimpt.com/ability/model/b163b9e6b8294ef2bca08b67455c38ce/predict'; // 用于开发环境连接生产环境测试

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
                const result = JSON.parse(res)['value'];
                const resultJson = JSON.parse(result);
                this.label = resultJson['predictions'][0]['label'];
                this.score = resultJson['predictions'][0]['score'];
                this.sending = false;
            })
            .catch((error) => {
                this.label = '出错啦...';
                this.sending = false;
            });
    }

}
