import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './fincial-entity.component.html',
})
export class FincialEntityComponent implements OnInit {

    sentence: string;
    result: string;
    sending = false;

    ngOnInit() {
    }

    classify() {
        // const url = location.protocol + '//' + location.host + '/ability/model/0c55322e483e4ab09b1636dd383dfea7/translate'; // 用于生产环境正式部署
        const url = 'https://cubeai.dimpt.com/ability/model/7e9fa3503b1e47e38779950f3de8e087/predict'; // 用于开发环境连接生产环境测试

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
                if (this.result.length === 0) {
                    this.result = '无金融实体名词';
                }
                this.sending = false;
            })
            .catch((error) => {
                this.result = '后台错误清稍后再试';
                this.sending = false;
            });
    }

}
