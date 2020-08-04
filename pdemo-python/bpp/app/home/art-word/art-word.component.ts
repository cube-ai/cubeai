import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './art-word.component.html',
})
export class ArtWordComponent implements OnInit {

    results: string[];
    style = 'fire';
    scale = false;
    scale_step = 5;
    word: string;
    error: boolean;
    sending = false;

    ngOnInit() {
    }

    transfer() {
        const url = 'https://cubeai.dimpt.com/ability/model/36a93dc130184c899b16a43fb17f4721/transfer';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'word': this.word,
                'style': this.style,
                'scale': this.scale ? -1 : 0,
                'scale_step': this.scale_step / 10.0,
            }),
            method: 'POST',
        };

        this.sending = true;
        this.error = false;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.results = JSON.parse(JSON.parse(res)['value'])['results'];
                this.sending = false;
            })
            .catch((error) => {
                this.error = true;
                this.sending = false;
            });
    }

}
