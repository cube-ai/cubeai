import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './demo.component.html',
})
export class DemoComponent implements OnInit {

    petalLength: number;
    petalWidth: number;
    sepalLength: number;
    sepalWidth: number;
    result: string;
    sending = false;

    ngOnInit() {
    }

    classify() {
        // const url = location.protocol + '//' + location.host + '/ability/model/4c5ea015d05f45b4abf8d3a824dbeaf8/classify'; // 用于生产环境正式部署
        const url = 'https://cubeai.dimpt.com/ability/model/4c5ea015d05f45b4abf8d3a824dbeaf8/classify'; // 用于开发环境连接生产环境测试

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'petal_length': [
                    this.petalLength,
                ],
                'petal_width': [
                    this.petalWidth,
                ],
                'sepal_length': [
                    this.sepalLength,
                ],
                'sepal_width': [
                    this.sepalWidth,
                ]
            }),
            method: 'POST',
        };

        this.sending = true;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.result = JSON.parse(res)['value'][0];
                switch (this.result[0]) {
                    case '1':
                        this.result = '山鸢尾';
                        break;
                    case '2':
                        this.result = '变色鸢尾';
                        break;
                    case '3':
                        this.result = '维吉尼亚鸢尾';
                        break;
                    default:
                        this.result = '不认识';
                }
                this.sending = false;
            })
            .catch((error) => {
                this.result = error;
                this.sending = false;
            });
    }

}
