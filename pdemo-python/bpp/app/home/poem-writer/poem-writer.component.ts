import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './poem-writer.component.html',
})
export class PoemWriterComponent implements OnInit {

    sentences: string[];
    word: string;
    error: boolean;
    sending = false;

    ngOnInit() {
    }

    write() {
        const url = 'https://cubeai.dimpt.com/ability/model/9fa0e4c95ede44dea49d6cf0178fe81e/write';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'first': this.word,
            }),
            method: 'POST',
        };

        this.sending = true;
        this.error = false;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                const poem = JSON.parse(res)['value'];
                this.sentences = poem.split('。');
                this.sending = false;
            })
            .catch((error) => {
                this.error = true;
                this.sending = false;
            });
    }

}
