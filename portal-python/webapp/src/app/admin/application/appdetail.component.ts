import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs';
import {Principal, User} from '../../shared';
import {Application} from '../../shared/model/application.model';
import {GlobalService, UaaClient} from '../../shared';
import {FileUploader} from 'ng2-file-upload';
import {v4 as uuid} from 'uuid';
import {MessageService} from 'primeng/api';
import {ImageCroppedEvent} from "ngx-image-cropper";

@Component({
    templateUrl: './appdetail.component.html',
})
export class AppdetailComponent implements OnInit {

    subscription: Subscription;
    user: User;
    application: Application;
    mode: string;
    headline: string;
    id: number;
    fileSelector: FileUploader;
    inputPictureUrl: string;

    constructor(private globalService: GlobalService,
                private principal: Principal,
                private location: Location,
                private route: ActivatedRoute,
                private router: Router,
                private uaaClient: UaaClient,
                private messageService: MessageService,
    ) {
    }

    goBack() {
        this.location.back();
    }

    ngOnInit() {
        this.user = this.principal.getCurrentAccount() ? this.principal.getCurrentAccount() : null;
        this.subscription = this.route.params.subscribe((params) => {
            this.mode = params['mode'];
            this.id = params['id'];

            if (!this.user.authorities.includes('ROLE_APPLICATION') && this.mode === 'create') {
                this.messageService.add({severity:'error', detail:'你没有权限创建应用！'});
                this.goBack();
            }

            if (!this.user.authorities.includes('ROLE_APPLICATION') && this.mode === 'edit') {
                this.messageService.add({severity:'error', detail:'你没有权限创建应用！'});
                this.mode = 'view';
            }

            if (this.mode === 'create') {
                this.createApplication();
            } else {
                this.loadApplication();
            }

            switch (this.mode) {
                case 'create':
                    this.headline = '新建应用';
                    break;
                case 'edit':
                    this.headline = '编辑应用';
                    break;
                case 'view':
                    this.headline = '查看应用';
                    break;
            }
        });

        this.fileSelector = new FileUploader({});
        this.fileSelector.onAfterAddingFile = (fileItem) => {
            if (!fileItem.file.type.startsWith('image')) {
                this.messageService.add({severity:'error', detail:'文件类型必须是图片！'});
                fileItem.remove();
                return;
            }

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
            this.inputPictureUrl = fileReader.result as string;
        };
    }

    saveCroppedImage(event: ImageCroppedEvent) {
        this.application.pictureUrl = event.base64;
    }

    loadApplication() {
        this.uaaClient.find_application({
            id: this.id,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.application = res.body['value'];
                } else {
                    this.messageService.add({severity:'error', detail:'获取应用出错！'});
                    this.goBack();
                }
            }, () => {
                this.messageService.add({severity:'error', detail:'获取应用出错！'});
                this.goBack();
            }
        );
    }

    createApplication() {
        this.application = new Application();
        this.application.uuid = uuid().replace(/-/g, '').toLowerCase();
    }

    saveApplication() {
        if (this.application.id) {
            this.uaaClient.update_application({
                application: this.application,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.goBack();
                    } else {
                        this.messageService.add({severity:'error', detail:'应用信息更新失败！'});
                    }
                }, () => {
                    this.messageService.add({severity:'error', detail:'应用信息更新失败！'});
                }
            );
        } else {
            this.uaaClient.create_application({
                application: this.application,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.goBack();
                    } else {
                        this.messageService.add({severity:'error', detail:'应用信息保存失败！'});
                    }
                }, () => {
                    this.messageService.add({severity:'error', detail:'应用信息保存失败！'});
                }
            );
        }
    }

    getRandomPicture() {
        if (Math.round(Math.random())) {
            this.uaaClient.get_random_picture({
                width: 300,
                height: 300,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.inputPictureUrl = res.body['value'];
                    }
                }
            );
        } else {
            this.uaaClient.get_random_avatar({
                size: 300,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.inputPictureUrl = res.body['value'];
                    }
                }
            );
        }
    }

}
