import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './ltespeed.component.html',
})
export class LtespeedComponent implements OnInit {

    lteSpecs: number[];
    lteSpecsText: string;
    speed: string;
    sending = false;
    lteSpecsFields = '';

    ngOnInit() {
        this.lteSpecs = [];
        this.lteSpecsText = this.lteSpecs.toString();
    }

    predict() {
        // const url = location.protocol + '//' + location.host + '/ability/model/9d5f03afe77c43d98cd1d5a107bbb3d0/predict'; // 用于生产环境正式部署
        const url = 'https://cubeai.dimpt.com/ability/model/9d5f03afe77c43d98cd1d5a107bbb3d0/predict'; // 用于开发环境连接生产环境测试

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'lte_specs': this.lteSpecs,
            }),
            method: 'POST',
        };

        this.speed = '';
        this.lteSpecs = this.lteSpecsText.split(',').map(Number);
        this.sending = true;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                const result = JSON.parse(res)['value'];
                this.speed = result === 1 ? '达到10Mb/s' : '不足10Mb/s';
                this.sending = false;
            })
            .catch((error) => {
                this.speed = '出错啦...';
                this.sending = false;
            });
    }

    genLteSpecsExample() {
        // const url = location.protocol + '//' + location.host + '/ability/model/9d5f03afe77c43d98cd1d5a107bbb3d0/gen_specs_example'; // 用于生产环境正式部署
        const url = 'https://cubeai.dimpt.com/ability/model/9d5f03afe77c43d98cd1d5a107bbb3d0/gen_specs_example'; // 用于开发环境连接生产环境测试

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'text': '',
            }),
            method: 'POST',
        };

        this.sending = true;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.lteSpecs = JSON.parse(res)['value'];
                this.lteSpecsText = this.lteSpecs.join(',');
                this.sending = false;
            })
            .catch((error) => {
                this.lteSpecsText = '出错啦...';
                this.sending = false;
            });
    }

}
