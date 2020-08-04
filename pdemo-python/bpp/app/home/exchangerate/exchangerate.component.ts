import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './exchangerate.component.html',
})
export class ExchangerateComponent implements OnInit {

    currency1: string;
    currency2: string;
    result: string;
    sending = false;

    ngOnInit() {
    }

    classify() {
        // const url = location.protocol + '//' + location.host + '/ability/model/534a56d1ffac4172b3181cc4c0c4ff88/query'; // 用于生产环境正式部署
        const url = 'https://cubeai.dimpt.com/ability/model/534a56d1ffac4172b3181cc4c0c4ff88/query'; // 用于开发环境连接生产环境测试

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'currency1': this.currency1,
                'currency2': this.currency2
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
