import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './weather.component.html',
})
export class WeatherComponent implements OnInit {

    cityname: string;
    results = [];
    sending = false;

    ngOnInit() {
    }

    classify() {
        const url = 'https://cubeai.dimpt.com/ability/model/5669ad764d9a4181aa88db2fabf27fc1/forecast';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'city': this.cityname
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

}
