import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/Subscription';
import {Principal, User} from '../../shared';
import {Application} from '../../shared/model/application.model';
import {GlobalService, SnackBarService, UaaClient} from '../../shared';
import {FileUploader} from 'ng2-file-upload';
import {v4 as uuid} from 'uuid';

@Component({
    templateUrl: './appdetail.component.html',
    styleUrls: [
        '../admin-datapage.css'
    ]
})
export class AppdetailComponent implements OnInit {

    subscription: Subscription;
    user: User;
    application: Application;
    mode: string;
    id: number;
    fileSelector: FileUploader;

    constructor(private globalService: GlobalService,
                private principal: Principal,
                private location: Location,
                private route: ActivatedRoute,
                private router: Router,
                private uaaClient: UaaClient,
                private snackBarService: SnackBarService,
    ) {
    }

    goBack() {
        this.location.back();
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }
        this.user = this.principal.getCurrentAccount() ? this.principal.getCurrentAccount() : null;
        this.subscription = this.route.params.subscribe((params) => {
            this.mode = params['mode'];
            this.id = params['id'];

            if (!this.user.authorities.includes('ROLE_APPLICATION') && this.mode === 'create') {
                this.snackBarService.error('你没有权限创建应用！');
                this.goBack();
            }

            if (!this.user.authorities.includes('ROLE_APPLICATION') && this.mode === 'edit') {
                this.snackBarService.error('你没有权限创建应用！');
                this.mode = 'view';
            }

            if (this.mode === 'create') {
                this.createApplication();
            } else {
                this.loadApplication();
            }
        });

        this.fileSelector = new FileUploader({});
        this.fileSelector.onAfterAddingFile = (fileItem) => {
            if (fileItem.file.size > 100 * 1024) {
                this.snackBarService.error('图片文件不能大于100KB！');
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
            this.application.pictureUrl = fileReader.result;
        };
    }

    loadApplication() {
        this.uaaClient.find_application({
            id: this.id,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.application = res.body['value'];
                } else {
                    this.snackBarService.error('获取应用出错：' + res.body['value']);
                }
            }, () => {
                this.snackBarService.error('获取应用出错！');
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
                        this.snackBarService.error('应用信息更新失败：' + res.body['value']);
                    }
                }, () => {
                    this.snackBarService.error('应用信息更新失败！');
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
                        this.snackBarService.error('应用信息保存失败：' + res.body['value']);
                    }
                }, () => {
                    this.snackBarService.error('应用信息保存失败！');
                }
            );
        }
    }

    getRandomPicture() {
        this.uaaClient.get_random_picture({
            width: 300,
            height: 300,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.application.pictureUrl = res.body['value'];
                }
            }
        );
    }

}
