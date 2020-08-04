import {Component, OnInit} from '@angular/core';
import {FileUploader} from 'ng2-file-upload';

@Component({
    templateUrl: './imgclassify.component.html',
})
export class ImgclassifyComponent implements OnInit {

    result: string;
    resultNum: string;
    sending = false;

    fileSelector: FileUploader;
    selectedImgDataUrl: string;

    ngOnInit() {
        this.resultNum = '';
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

    classify() {
        this.result = null;
        if (this.selectedImgDataUrl) {
            const url = 'https://cubeai.dimpt.com/ability/model/ac38903a028f42988e28c335c88298e2/classify';

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
                this.result = JSON.parse(res)['value'];
                switch (this.result[0]) {
                    case '0':
                        this.resultNum = '飞机';
                        break;
                    case '1':
                        this.resultNum = '汽车';
                        break;
                    case '2':
                        this.resultNum = '鸟';
                        break;
                    case '3':
                        this.resultNum = '猫';
                        break;
                    case '4':
                        this.resultNum = '鹿';
                        break;
                    case '5':
                        this.resultNum = '狗';
                        break;
                    case '6':
                        this.resultNum = '蛙';
                        break;
                    case '7':
                        this.resultNum = '马';
                        break;
                    case '8':
                        this.resultNum = '船';
                        break;
                    case '9':
                        this.resultNum = '卡车';
                        break;
                    default:
                        this.resultNum = '图片错误';
                }
                this.sending = false;
            }).catch((error) => {
                this.resultNum = error;
                this.sending = false;
            });
        }
    }
}
