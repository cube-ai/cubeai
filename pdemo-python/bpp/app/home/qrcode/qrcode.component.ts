import {Component, OnInit} from '@angular/core';
import {FileUploader} from 'ng2-file-upload';

@Component({
    templateUrl: './qrcode.component.html',
})
export class QrcodeComponent implements OnInit {

    text1: string;
    img_url1: string;
    text2: string;
    img_url2: string;
    sending1 = false;
    sending2 = false;
    fileSelector: FileUploader;

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
            this.img_url2 = fileReader.result;
            this.scan();
        };
    }

    make() {
        const url = 'https://cubeai.dimpt.com/ability/model/597f0ef3dd334d18b45c299009a0d11d/make_url';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'text': this.text1
            }),
            method: 'POST',
        };

        this.sending1 = true;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.img_url1 = JSON.parse(res)['value'];
                this.sending1 = false;
            })
            .catch((error) => {
                this.img_url1 = '出错啦...';
                this.sending1 = false;
            });
    }

    scan() {
        const url = 'https://cubeai.dimpt.com/ability/model/597f0ef3dd334d18b45c299009a0d11d/scan_url';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'img_url': this.img_url2
            }),
            method: 'POST',
        };

        this.sending2 = true;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.text2 = JSON.parse(res)['value'];
                this.sending2 = false;
            })
            .catch((error) => {
                this.text2 = '出错啦...';
                this.sending2 = false;
            });
    }

}
