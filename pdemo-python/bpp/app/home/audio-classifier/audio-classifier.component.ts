import {Component, OnInit} from '@angular/core';
import {FileUploader} from 'ng2-file-upload';

@Component({
    templateUrl: './audio-classifier.component.html',
})
export class AudioClassifierComponent implements OnInit {

    result: string;
    sending = false;
    error: boolean;
    results = [];
    fileSelector: FileUploader;
    selectedImgDataUrl: string;

    ngOnInit() {
        this.fileSelector = new FileUploader({});
        this.fileSelector.onAfterAddingFile = (fileItem) => {
            if (this.fileSelector.queue.length > 1) {
                this.fileSelector.queue[0].remove();
            }
            this.readImgFile(fileItem._file);
        };
    }

    readImgFile(file: File) {
        const fileReader = new FileReader();
        fileReader.readAsDataURL(file);
        fileReader.onload = () => {
            this.selectedImgDataUrl = fileReader.result;
            this.classify();
        };
    }

    genTestImage() {
        // const url = location.protocol + '//' + location.host + '/ability/model/a446a25e012142e1ace33e0bc5e7b936/gen_test_img'; // 用于生产环境正式部署
        const url = 'https://cubeai.dimpt.com/ability/model/bc82dd0e354b422b962f73a528b8b180/gen_test_wav'; // 用于生产环境测试和正式部署

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'text': ''
            }),
            method: 'POST',
        };

        this.result = null;
        this.sending = true;
        fetch(url, params).then((data) => data.text()
        ).then((res) => {
            this.selectedImgDataUrl = 'data:audio/wav;base64,,' + JSON.parse(res)['value'];
            this.sending = false;
            this.classify();
        }).catch((error) => {
            this.sending = false;
        });
    }

    classify() {

        this.results = [];
        if (this.selectedImgDataUrl) {
            // const url = location.protocol + '//' + location.host + '/ability/model/a446a25e012142e1ace33e0bc5e7b936/classify'; // 用于生产环境正式部署
            const url = 'https://cubeai.dimpt.com/ability/model/bc82dd0e354b422b962f73a528b8b180/classify'; // 用于开发环境连接生产环境测试
            console.log('url', this.selectedImgDataUrl);
            const params = {
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: JSON.stringify({
                    'wav_base64': this.selectedImgDataUrl.substring(this.selectedImgDataUrl.indexOf('base64') + 7)
                }),
                method: 'POST',
            };

            this.result = null;
            this.sending = true;
            fetch(url, params).then((data) => data.text()
            ).then((res) => {
                this.results = JSON.parse(JSON.parse(res)['value']);
                this.sending = false;
            }).catch((error) => {
                this.result = '出错啦...';
                this.sending = false;
            });
        }
    }

}
