import {Component, OnInit} from '@angular/core';
import {FileUploader} from 'ng2-file-upload';

@Component({
    templateUrl: './ocrwordcloud.component.html',
})
export class OcrWordCloudComponent implements OnInit {

    result: string;
    sending = false;
    error: boolean;

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
            this.predict();
        };
    }
    predict() {
        this.error = false;

        // const url = 'https://cubeai.dimpt.com/ability/model/4265fb432ce74623ae9d7b425e962161/forecast'; // 用于生产环境正式部署
        const url = location.protocol + '//' + location.host + '/ability/model/938d5d8c943c436c9e04bc26647f89b4/ocr'; // 用于开发环境连接生产环境测试

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'img_base64': this.selectedImgDataUrl.substring(this.selectedImgDataUrl.indexOf('base64') + 7)
            }),
            method: 'POST',
        };

        this.sending = true;
        fetch(url, params).then((data) => data.text()
        ).then((res) => {
            this.result = (JSON.parse(res)['value']);
            this.sending = false;
        }).catch((error) => {
            this.error = true;
            this.sending = false;
        });
    }

}
