import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {GlobalService, SnackBarService} from '../../shared';
import {Principal} from '../../account';
import {FileUploader} from 'ng2-file-upload';
import {FileItem} from 'ng2-file-upload/file-upload/file-item.class';
import {HttpClient} from '@angular/common/http';
import {CookieService} from 'ngx-cookie';
import {SERVER_API_URL} from '../../app.constants';
import {v4 as uuid} from 'uuid';
import {OnboardingService, TaskStepService} from '../';
import {TaskStep} from '../model/task-step.model';

@Component({
    templateUrl: './onboarding.component.html',
    styleUrls: [
        '../ucumos-datapage.css'
    ]
})
export class OnboardingComponent implements OnInit, OnDestroy {

    userLogin: string;
    taskUuid: string;
    uploader: FileUploader;
    onboardingStarted = false;

    progressExtractModelFile = 0;
    progressCreateSolution = 0;
    progressAddArtifact = 0;
    progressCreateTosca = 0;
    progressGenerateMicroService = 0;

    detailExtractModelFile = '';
    detailCreateSolution = '';
    detailAddArtifact = '';
    detailCreateTosca = '';
    detailGenerateMicroService = '';

    statusCreateTaskError = false;
    statusExtractModelFile = '';
    statusCreateSolution = '';
    statusAddArtifact = '';
    statusCreateTosca = '';
    statusGenerateMicroService = '';

    timer: any;
    lastId = 0;
    pullingTaskSteps = false;

    constructor(
        private globalService: GlobalService,
        private router: Router,
        private principal: Principal,
        private http: HttpClient,
        private cookieService: CookieService,
        private snackBarService: SnackBarService,
        private onboardingService: OnboardingService,
        private taskStepService: TaskStepService,
    ) {
    }

    ngOnDestroy() {
        if (this.onboardingStarted && !this.statusCreateTaskError) {
            clearInterval(this.timer);
            this.snackBarService.info('你已离开[模型导入]页面。之后你还可以在[我的任务]中继续查看任务执行情况...');
        } else {
            this.onboardingService.deleteModelFile(this.taskUuid).subscribe();
        }
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }

        this.userLogin = this.principal.getCurrentAccount().login;
        this.taskUuid = uuid().replace(/-/g, '').toLowerCase();
        this.uploader = new FileUploader({
            url: SERVER_API_URL + 'zuul/umu/api/modelfile/' + this.taskUuid,
            method: 'POST',
            itemAlias: this.userLogin,
            queueLimit: 1,
        });

        this.uploader.onBeforeUploadItem = (fileItem) => {
            fileItem.headers.push({name: 'X-XSRF-TOKEN', value: this.cookieService.get('XSRF-TOKEN')});
            return fileItem;
        };
    }

    upload(fileItem: FileItem) {
        fileItem.onSuccess = (response, status, headers) => {
            if (status === 200) {
                this.snackBarService.success('文件上传成功！');
            } else {
                this.snackBarService.success('文件上传失败！');
            }
        };

        if (fileItem.file.size > 500 * 1024 * 1024) {
            this.snackBarService.error('上传模型压缩包文件不能大于500MB！');
            fileItem.remove();
            return;
        }

        fileItem.upload();
    }

    onboarding() {
        this.onboardingService.onboarding(this.taskUuid).subscribe(
            () => {
                this.timer = setInterval(() => {
                    this.displayProgress();
                }, 1000);
            }, () => {
                this.statusCreateTaskError = true;
            }
        );
        this.onboardingStarted = true;
    }

    displayProgress() {

        if (this.pullingTaskSteps) {
            return;
        }
        this.pullingTaskSteps = true;

        if (this.getCompleteSuccess() || this.getCompleteFail()) {
            clearInterval(this.timer);
        }

        if (this.progressExtractModelFile < 100) {
            this.taskStepService.query({
                id: this.lastId,
                taskUuid: this.taskUuid,
                stepName: '提取模型文件',
            }).subscribe(
                (res) => {
                    const taskSteps: TaskStep[] = res.body;
                    for (let i = 0; i < taskSteps.length; i++) {
                        const taskStep: TaskStep = taskSteps[i];
                        this.statusExtractModelFile = taskStep.stepStatus;
                        this.progressExtractModelFile = taskStep.stepProgress;
                        this.detailExtractModelFile += (taskStep.description + '\n');
                        this.lastId = taskStep.id;
                    }
                    this.pullingTaskSteps = false;
                    return;
                }
            );
        } else if (this.progressExtractModelFile === 100 && this.statusExtractModelFile === '成功' && this.progressCreateSolution < 100) {
            this.taskStepService.query({
                id: this.lastId,
                taskUuid: this.taskUuid,
                stepName: '创建模型对象',
            }).subscribe(
                (res) => {
                    const taskSteps: TaskStep[] = res.body;
                    for (let i = 0; i < taskSteps.length; i++) {
                        const taskStep: TaskStep = taskSteps[i];
                        this.statusCreateSolution = taskStep.stepStatus;
                        this.progressCreateSolution = taskStep.stepProgress;
                        this.detailCreateSolution += (taskStep.description + '\n');
                        this.lastId = taskStep.id;
                    }
                    this.pullingTaskSteps = false;
                    return;
                }
            );
        } else if (this.progressCreateSolution === 100 && this.statusCreateSolution === '成功' && this.progressAddArtifact < 100) {
            this.taskStepService.query({
                id: this.lastId,
                taskUuid: this.taskUuid,
                stepName: '添加artifact',
            }).subscribe(
                (res) => {
                    const taskSteps: TaskStep[] = res.body;
                    for (let i = 0; i < taskSteps.length; i++) {
                        const taskStep: TaskStep = taskSteps[i];
                        this.statusAddArtifact = taskStep.stepStatus;
                        this.progressAddArtifact = taskStep.stepProgress;
                        this.detailAddArtifact += (taskStep.description + '\n');
                        this.lastId = taskStep.id;
                    }
                    this.pullingTaskSteps = false;
                    return;
                }
            );
        } else if (this.progressAddArtifact === 100 && this.statusAddArtifact === '成功' && this.progressCreateTosca < 100) {
            this.taskStepService.query({
                id: this.lastId,
                taskUuid: this.taskUuid,
                stepName: '生成TOSCA文件',
            }).subscribe(
                (res) => {
                    const taskSteps: TaskStep[] = res.body;
                    for (let i = 0; i < taskSteps.length; i++) {
                        const taskStep: TaskStep = taskSteps[i];
                        this.statusCreateTosca = taskStep.stepStatus;
                        this.progressCreateTosca = taskStep.stepProgress;
                        this.detailCreateTosca += (taskStep.description + '\n');
                        this.lastId = taskStep.id;
                    }
                    this.pullingTaskSteps = false;
                    return;
                }
            );
        } else if (this.progressCreateTosca === 100 && this.statusCreateTosca === '成功' && this.progressGenerateMicroService < 100) {
            this.taskStepService.query({
                id: this.lastId,
                taskUuid: this.taskUuid,
                stepName: '创建微服务',
            }).subscribe(
                (res) => {
                    const taskSteps: TaskStep[] = res.body;
                    for (let i = 0; i < taskSteps.length; i++) {
                        const taskStep: TaskStep = taskSteps[i];
                        this.statusGenerateMicroService = taskStep.stepStatus;
                        this.progressGenerateMicroService = taskStep.stepProgress;
                        this.detailGenerateMicroService += (taskStep.description + '\n');
                        this.lastId = taskStep.id;
                    }
                    this.pullingTaskSteps = false;
                    return;
                }
            );
        }
    }

    getCompleteSuccess(): boolean {
        return this.statusGenerateMicroService === '成功';
    }

    getCompleteFail(): boolean {
        return this.statusExtractModelFile === '失败' || this.statusCreateSolution === '失败'
            || this.statusAddArtifact === '失败' || this.statusCreateTosca === '失败'
            || this.statusGenerateMicroService === '失败';
    }

    jumpToMyModel() {
        this.router.navigate(['/ucumos/solution/' + this.taskUuid + '/' + 'edit']);
    }

}
