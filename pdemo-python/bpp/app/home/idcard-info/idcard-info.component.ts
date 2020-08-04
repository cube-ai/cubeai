import {Component, OnInit} from '@angular/core';
import {FileUploader} from 'ng2-file-upload';

@Component({
    templateUrl: './idcard-info.component.html',
})
export class IdcardInfoComponent implements OnInit {

    id_info = null;
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
            this.predict();
        };
    }

    predict() {
        const url = 'https://cubeai.dimpt.com/ability/model/bafa518c37fc4507bf4ae08bdf32cd82/recognize';

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

        this.id_info = null;
        this.error = false;
        this.sending = true;
        fetch(url, params).then((data) => data.text()
        ).then((res) => {
            this.id_info = JSON.parse(JSON.parse(res)['value'])['id_info'];
            this.sending = false;
        }).catch((error) => {
            this.error = true;
            this.sending = false;
        });
    }

}
