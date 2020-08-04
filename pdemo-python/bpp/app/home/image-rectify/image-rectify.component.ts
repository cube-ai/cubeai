import {Component, OnInit} from '@angular/core';
import {FileUploader} from 'ng2-file-upload';

@Component({
    templateUrl: './image-rectify.component.html',
})
export class ImageRectifyComponent implements OnInit {

    img_url: string;
    error = false;
    sending = false;

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
            this.colorize();
        };
    }

    colorize() {
        this.error = false;
        this.img_url = null;

        const url = 'https://cubeai.dimpt.com/ability/model/18a4993da4064626b388bcc428a5dede/rectify'; // 用于开发环境连接生产环境测试

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
            this.img_url = JSON.parse(JSON.parse(res)['value'])['img_url'];
            this.sending = false;
        }).catch((error) => {
            this.error = true;
            this.sending = false;
        });
    }

}
