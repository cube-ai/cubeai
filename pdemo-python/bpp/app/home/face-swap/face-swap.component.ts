import {Component, OnInit} from '@angular/core';
import {FileUploader} from 'ng2-file-upload';

@Component({
    templateUrl: './face-swap.component.html',
})
export class FaceSwapComponent implements OnInit {

    outputImgUrl: string;
    error: boolean;
    sending = false;

    fileSelector1: FileUploader;
    fileSelector2: FileUploader;
    inputImgUrl1: string;
    inputImgUrl2: string;

    ngOnInit() {
        this.fileSelector1 = new FileUploader({});
        this.fileSelector1.onAfterAddingFile = (fileItem) => {
            if (this.fileSelector1.queue.length > 1) {
                this.fileSelector1.queue[0].remove();
            }
            this.readImgFile1(fileItem._file);
        };

        this.fileSelector2 = new FileUploader({});
        this.fileSelector2.onAfterAddingFile = (fileItem) => {
            if (this.fileSelector2.queue.length > 1) {
                this.fileSelector2.queue[0].remove();
            }
            this.readImgFile2(fileItem._file);
        };
    }

    readImgFile1(file: File) {
        const fileReader = new FileReader();
        fileReader.readAsDataURL(file);
        fileReader.onload = () => {
            this.inputImgUrl1 = fileReader.result;
        };
    }

    readImgFile2(file: File) {
        const fileReader = new FileReader();
        fileReader.readAsDataURL(file);
        fileReader.onload = () => {
            this.inputImgUrl2 = fileReader.result;
        };
    }

    swap_face() {
        this.error = false;
        this.outputImgUrl = null;

        const url = 'https://cubeai.dimpt.com/ability/model/cda53f8505974569a1a59d3bac58dd08/swap_face';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'img1_base64': this.inputImgUrl1.substring(this.inputImgUrl1.indexOf('base64') + 7),
                'img2_base64': this.inputImgUrl2.substring(this.inputImgUrl2.indexOf('base64') + 7)
            }),
            method: 'POST',
        };

        this.sending = true;
        fetch(url, params).then((data) => data.text()
        ).then((res) => {
            this.outputImgUrl = JSON.parse(res)['value'];
            this.sending = false;
        }).catch((error) => {
            this.error = true;
            this.sending = false;
        });
    }

}
