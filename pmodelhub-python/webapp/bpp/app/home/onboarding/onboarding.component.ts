import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import { Location } from '@angular/common';
import {GlobalService, SnackBarService, HttpService} from '../../shared';
import {Principal} from '../../shared';
import {FileUploader} from 'ng2-file-upload';
import {FileItem} from 'ng2-file-upload/file-upload/file-item.class';
import {SERVER_API_URL} from '../../app.constants';
import {TaskStep} from '../model/task-step.model';

@Component({
    templateUrl: './onboarding.component.html',
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

    viewDetailExtractModelFile = true;
    viewDetailCreateSolution = true;
    viewDetailAddArtifact = true;
    viewDetailCreateTosca = true;
    viewDetailGenerateMicroService = true;

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
        private location: Location,
        private principal: Principal,
        private snackBarService: SnackBarService,
        private http: HttpService,
    ) {
    }

    ngOnDestroy() {
        if (this.onboardingStarted) {
            clearInterval(this.timer);
            this.snackBarService.info('你已离开[模型导入]页面。之后你还可以在[我的任务]中继续查看任务执行情况...');
        }
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();

            this.uploader = new FileUploader({
                url: SERVER_API_URL + 'umu/api/file/onboard_model',
                method: 'POST',
                itemAlias: 'onboard_model',
                queueLimit: 1,
            });

            this.uploader.onAfterAddingFile = (fileItem) => {
                this.upload(fileItem);

            };
        });
    }

    upload(fileItem: FileItem) {
        fileItem.onSuccess = (response, status, headers) => {
            if (status === 200) {
                const result = JSON.parse(response);
                if (result['status'] === 'ok') {
                    this.snackBarService.success('文件上传成功！');
                    this.taskUuid = result['value'];
                    this.startOnboarding();
                } else {
                    this.snackBarService.success('文件上传失败！');
                }
            } else {
                this.snackBarService.success('文件上传失败！');
            }
        };

        if (fileItem.file.size > 800 * 1024 * 1024) {
            this.snackBarService.error('上传模型压缩包文件不能大于800MB！');
            fileItem.remove();
            return;
        }

        fileItem.upload();
    }

    startOnboarding() {
        this.timer = setInterval(
            () => {
                this.displayProgress();
            },
            1000
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
            const body = {
                action: 'get_task_steps',
                args: {
                    lastId: this.lastId,
                    taskUuid: this.taskUuid,
                    stepName: '提取模型文件',
                },
            };
            this.http.post('umm', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        const taskSteps: TaskStep[] = res.body['value'];
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
                }
            );
        } else if (this.progressExtractModelFile === 100 && this.statusExtractModelFile === '成功' && this.progressCreateSolution < 100) {
            const body = {
                action: 'get_task_steps',
                args: {
                    lastId: this.lastId,
                    taskUuid: this.taskUuid,
                    stepName: '创建模型对象',
                },
            };
            this.http.post('umm', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        const taskSteps: TaskStep[] = res.body['value'];
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
                }
            );
        } else if (this.progressCreateSolution === 100 && this.statusCreateSolution === '成功' && this.progressAddArtifact < 100) {
            const body = {
                action: 'get_task_steps',
                args: {
                    lastId: this.lastId,
                    taskUuid: this.taskUuid,
                    stepName: '添加artifact',
                },
            };
            this.http.post('umm', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        const taskSteps: TaskStep[] = res.body['value'];
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

                }
            );
        } else if (this.progressAddArtifact === 100 && this.statusAddArtifact === '成功' && this.progressCreateTosca < 100) {
            const body = {
                action: 'get_task_steps',
                args: {
                    lastId: this.lastId,
                    taskUuid: this.taskUuid,
                    stepName: '生成TOSCA文件',
                },
            };
            this.http.post('umm', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        const taskSteps: TaskStep[] = res.body['value'];
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

                }
            );
        } else if (this.progressCreateTosca === 100 && this.statusCreateTosca === '成功' && this.progressGenerateMicroService < 100) {
            const body = {
                action: 'get_task_steps',
                args: {
                    lastId: this.lastId,
                    taskUuid: this.taskUuid,
                    stepName: '创建微服务',
                },
            };
            this.http.post('umm', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        const taskSteps: TaskStep[] = res.body['value'];
                        for (let i = 0; i < taskSteps.length; i++) {
                            const taskStep: TaskStep = taskSteps[i];
                            this.statusGenerateMicroService = taskStep.stepStatus;
                            this.progressGenerateMicroService = taskStep.stepProgress;
                            this.detailGenerateMicroService += (taskStep.description + '\n');
                            this.lastId = taskStep.id;
                        }

                        if (this.progressGenerateMicroService >= 40 && this.progressGenerateMicroService < 69) {
                            this.progressGenerateMicroService += 1;
                            this.detailGenerateMicroService += '正在创建微服务docker镜像...\n';
                            if (this.progressGenerateMicroService === 69) {
                                this.progressGenerateMicroService = 40;
                            }
                        }

                        this.pullingTaskSteps = false;
                        return;
                    }
                }
            );
        }
    }

    toggleViewDetailExtractModelFile() {
        this.viewDetailExtractModelFile = !this.viewDetailExtractModelFile;
    }

    toggleViewDetailCreateSolution() {
        this.viewDetailCreateSolution = !this.viewDetailCreateSolution;
    }

    toggleViewDetailAddArtifact() {
        this.viewDetailAddArtifact = !this.viewDetailAddArtifact;
    }

    toggleViewDetailCreateTosca() {
        this.viewDetailCreateTosca = !this.viewDetailCreateTosca;
    }

    toggleViewDetailGenerateMicroService() {
        this.viewDetailGenerateMicroService = !this.viewDetailGenerateMicroService;
    }

    getCompleteSuccess(): boolean {
        return this.statusGenerateMicroService === '成功';
    }

    getCompleteFail(): boolean {
        return this.statusExtractModelFile === '失败' || this.statusCreateSolution === '失败'
            || this.statusAddArtifact === '失败' || this.statusCreateTosca === '失败'
            || this.statusGenerateMicroService === '失败';
    }

    gotoMyModel() {
        this.router.navigate(['/solution/' + this.taskUuid]);
    }

    gotoPackagingHelp() {
        this.router.navigate(['/packaging']);
    }

}
