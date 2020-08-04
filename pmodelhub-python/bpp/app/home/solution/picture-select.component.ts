import {Component, OnInit, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import {FileUploader} from 'ng2-file-upload';
import {SnackBarService, RandomPictureService} from '../../shared';
import {DownloadService} from '../';

@Component({
    templateUrl: './picture-select.component.html',
})
export class PictureSelectComponent implements OnInit {

    imgUriPrefix = window.location.protocol + '//' + window.location.host + window.location.pathname + 'content/images/pictures/';
    imgNames = ['angular.jpg', 'docker.jpg', 'face.jpg', 'go.jpg', 'h2o.jpg', 'h2o2.jpg', 'java.jpg', 'matlab.jpg', 'nodejs.jpg',
        'python.jpg', 'r.jpg', 'ruby.jpg', 'rust.jpg', 'scala.jpg', 'swift.jpg', 'tensorflow.jpg'];

    fileSelect: FileUploader;
    selectedImgDataUrl: string;

    constructor(
        public dialogRef: MatDialogRef<PictureSelectComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private snackBarService: SnackBarService,
        private downloadService: DownloadService,
        private randomPictureService: RandomPictureService,
    ) {
    }

    ngOnInit() {
        this.selectedImgDataUrl = this.data.pictureUrl;

        this.fileSelect = new FileUploader({});
        this.fileSelect.onAfterAddingFile = (fileItem) => {
            if (fileItem.file.size > 20 * 1024) {
                this.snackBarService.error('图片文件不能大于20KB！');
                fileItem.remove();
                return;
            }

            if (this.fileSelect.queue.length > 1) {
                this.fileSelect.queue[0].remove();
            }

            this.readImgFile(fileItem._file);
        };
    }

    readImgFile(file: File) {
        const fileReader = new FileReader();
        fileReader.readAsDataURL(file);
        fileReader.onload = () => {
            this.selectedImgDataUrl = fileReader.result;
        };
    }

    getFileFromWebImage(uri, fileName, mimeType) {
        return (fetch(uri)
                .then((res) => {
                    return res.arrayBuffer();
                })
                .then((buf) => {
                    return new File([buf], fileName, {type: mimeType});
                })
        );
    }

    selectImg(imgName: string) {
        this.getFileFromWebImage(this.imgUriPrefix + imgName, imgName, 'image/png').then(
            (file) => {
                this.readImgFile(file);
            }
        );
    }

    getRandomPicture() {
        this.randomPictureService.getRandomPicture(200, 200).subscribe(
            (res) => {
                this.selectedImgDataUrl = res.body.pictureDataUrl;
            }
        );
    }

    onSave(): void {
        this.dialogRef.close(this.selectedImgDataUrl);
    }

    onClose(): void {
        this.dialogRef.close();
    }

}
