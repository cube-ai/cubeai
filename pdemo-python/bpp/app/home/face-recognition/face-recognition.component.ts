import {Component, OnInit} from '@angular/core';
import {FileUploader} from 'ng2-file-upload';

@Component({
    templateUrl: './face-recognition.component.html',
})
export class FaceRecognitionComponent implements OnInit {

    outputImgUrl: string;
    faces = [];
    error: boolean;
    sending1 = false;
    sending2 = false;

    name = '';
    result = '';

    fileSelector: FileUploader;
    inputImgUrl: string;

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
            this.inputImgUrl = fileReader.result;
            this.predict();
        };
    }

    predict() {
        this.error = false;
        this.outputImgUrl = null;
        this.faces = [];
        this.name = '';
        this.result = '';

        // const url = location.protocol + '//' + location.host + '/ability/model/41f8e81e6a114278aa74d00f5ff01aa3/predict'; // 用于生产环境正式部署
        const url = 'https://cubeai.dimpt.com/ability/model/41f8e81e6a114278aa74d00f5ff01aa3/predict'; // 用于开发环境连接生产环境测试

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'img_base64': this.inputImgUrl.substring(this.inputImgUrl.indexOf('base64') + 7)
            }),
            method: 'POST',
        };

        this.sending1 = true;
        fetch(url, params).then((data) => data.text()
        ).then((res) => {
            const response = JSON.parse(JSON.parse(res)['value']);
            this.outputImgUrl = response['img_url'];
            this.faces = response['result'];
            this.sending1 = false;
        }).catch((error) => {
            this.error = true;
            this.sending1 = false;
        });
    }

    add_face() {
        // const url = location.protocol + '//' + location.host + '/ability/model/41f8e81e6a114278aa74d00f5ff01aa3/add_face'; // 用于生产环境正式部署
        const url = 'https://cubeai.dimpt.com/ability/model/41f8e81e6a114278aa74d00f5ff01aa3/add_face'; // 用于开发环境连接生产环境测试

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'name': this.name,
                'img_base64': this.inputImgUrl.substring(this.inputImgUrl.indexOf('base64') + 7),
            }),
            method: 'POST',
        };

        this.result = '正在添加人脸...';
        this.sending2 = true;
        fetch(url, params).then((data) => data.text()
        ).then((res) => {
            const result = JSON.parse(JSON.parse(res)['value']);
            if (result === 1) {
                this.result = '向后台数据库添加人脸成功!正在重新识别...';
                this.predict();
            } else if (result === 0) {
                this.result = '图片中没有人脸或有多于一个人脸，不能添加！';
            } else if (result === -1) {
                this.result = '后台数据库中已存在相似人脸，不能重复添加！';
            } else if (result === -2) {
                this.result = '添加失败！';
            }
            this.sending2 = false;
        }).catch((error) => {
            this.result = '向后台数据库添加人脸出错...';
            this.sending2 = false;
        });
    }

}
