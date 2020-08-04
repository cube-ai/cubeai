import {Component, OnInit} from '@angular/core';
import {FileUploader} from 'ng2-file-upload';

@Component({
    templateUrl: './opm.component.html',
})
export class OpmComponent implements OnInit {

    result: string;
    sending = false;
    baudRate: string;
    singleType: string;

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
        const url = 'https://cubeai.dimpt.com/ability/model/05697c2b797c4e49ba1cb08f0d17af37/gen_test_img';
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
            this.selectedImgDataUrl = 'data:image/jpeg;base64,' + JSON.parse(res)['value'];
            this.sending = false;
            this.classify();
        }).catch((error) => {
            this.sending = false;
        });
    }

    classify() {
        if (this.selectedImgDataUrl) {
            const url = 'https://cubeai.dimpt.com/ability/model/05697c2b797c4e49ba1cb08f0d17af37/predict'; // 用于开发环境连接生产环境测试

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

            this.result = null;
            this.sending = true;
            fetch(url, params).then((data) => data.text()
            ).then((res) => {
                const results = (JSON.parse(res)['value']).split('_');
                this.baudRate = results[0];
                this.singleType = results[1];
                this.result = JSON.parse(res)['value'] + '';
                this.sending = false;
            }).catch((error) => {
                this.result = '出错啦...';
                this.sending = false;
            });
        }
    }

}
