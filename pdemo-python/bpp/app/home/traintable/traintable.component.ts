import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './traintable.component.html',
})
export class TraintableComponent implements OnInit {

    startCity: string;
    targetCity: string;
    trainDate = new Date();
    minDate = new Date();
    maxDate = new Date();
    results = [];
    sending = false;

    ngOnInit() {
        this.maxDate.setDate(this.minDate.getDate() + 7);
    }

    classify() {
        // const url = location.protocol + '//' + location.host + '/ability/model/4e83fa884ad74a05836d83dd72b04dbd/query'; // 用于生产环境正式部署
        const url = 'https://cubeai.dimpt.com/ability/model/4e83fa884ad74a05836d83dd72b04dbd/query'; // 用于开发环境连接生产环境测试

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'from_station': this.startCity,
                'to_station': this.targetCity,
                'train_date': this.getDateText(this.trainDate)
            }),
            method: 'POST',
        };

        this.sending = true;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.results = JSON.parse(res)['value'];
                this.sending = false;
            })
            .catch((error) => {
                this.results = [];
                this.results.push(error);
                this.sending = false;
            });

    }

    getDateText(date: Date): string {
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        const day = date.getDate();

        const monthText = month < 10 ? '0' + month : '' + month;
        const dayText = day < 10 ? '0' + day : '' + day;

        return date.getFullYear() + '-' + monthText + '-' + dayText;
    }

}
