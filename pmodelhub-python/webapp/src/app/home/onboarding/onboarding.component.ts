import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import { Location } from '@angular/common';
import {Principal, GlobalService} from '../../shared';
import {UmmClient} from '../service/umm_client.service';
import {FileUploader} from 'ng2-file-upload';
import {FileItem} from 'ng2-file-upload/file-upload/file-item.class';
import {SERVER_API_URL} from '../../app.constants';
import {TaskStep} from '../model/task-step.model';
import {MessageService} from 'primeng/api';

@Component({
    templateUrl: './onboarding.component.html',
})
export class OnboardingComponent implements OnInit, OnDestroy {

    userLogin: string;
    taskUuid: string;
    uploader: FileUploader;
    onboardingStarted = false;

    websocket: WebSocket;
    taskSteps: TaskStep[] = [];

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
        private messageService: MessageService,
        private ummClient: UmmClient,
    ) {
    }

    ngOnDestroy() {
        if (this.onboardingStarted) {
            clearInterval(this.timer);
            this.websocket.close();
            this.messageService.add({severity:'info', detail:'你已离开[模型导入]页面。之后你还可以在[我的任务]中继续查看任务执行情况...'});
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
                    this.messageService.add({severity:'success', detail:'文件上传成功'});
                    this.taskUuid = result['value'];
                    this.startOnboarding();
                } else {
                    this.messageService.add({severity:'error', detail:'文件上传失败！'});
                }
            } else {
                this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
            }
        };

        if (fileItem.file.size > 800 * 1024 * 1024) {
            this.messageService.add({severity:'error', detail:'上传模型压缩包文件不能大于800MB！'});
            fileItem.remove();
            return;
        }

        fileItem.upload();
    }

    startOnboarding() {
        this.timer = setInterval(
            () => {
                this.displayProgressWithWebsocket();
            },
            1000
        );
        this.connectWebSocket();
        this.onboardingStarted = true;
    }

    connectWebSocket() {
        if (this.websocket != null) {
            this.websocket.close();
        }

        this.websocket = new WebSocket((window.location.protocol).replace(/http/, 'ws') + '//' + window.location.host + '/websocket');
        const that = this;
        this.websocket.onopen = () => {
            that.websocket.send(JSON.stringify({
                'type': 'subscribe',
                'content': 'task_' + this.taskUuid,
            }));
        };
        this.websocket.onmessage = (event) => {
            const msg = JSON.parse(event.data);
            if (msg['type'] === 'data') {
                this.taskSteps.push(msg['content']['task_step']);
                this.displayProgressWithWebsocket()
            }
        };
    }

    displayProgressWithWebsocket() {

        if (this.pullingTaskSteps) {
            return;
        }
        this.pullingTaskSteps = true;

        if (this.getCompleteSuccess() || this.getCompleteFail()) {
            clearInterval(this.timer);
        }

        if (this.progressExtractModelFile < 100) {
            const len = this.taskSteps.length;
            for (let i = 0; i < len; i++) {
                const taskStep = this.taskSteps[i];
                if (taskStep.stepName === '提取模型文件' && taskStep.id > this.lastId ) {
                    this.statusExtractModelFile = taskStep.stepStatus;
                    this.progressExtractModelFile = taskStep.stepProgress;
                    this.detailExtractModelFile += (taskStep.description + '\n');
                    this.lastId = taskStep.id;
                }
            }
            this.pullingTaskSteps = false;
            return;
        } else if (this.progressExtractModelFile === 100 && this.statusExtractModelFile === '成功' && this.progressCreateSolution < 100) {
            const len = this.taskSteps.length;
            for (let i = 0; i < len; i++) {
                const taskStep = this.taskSteps[i];
                if (taskStep.stepName === '创建模型对象' && taskStep.id > this.lastId) {
                    this.statusCreateSolution = taskStep.stepStatus;
                    this.progressCreateSolution = taskStep.stepProgress;
                    this.detailCreateSolution += (taskStep.description + '\n');
                    this.lastId = taskStep.id;
                }
            }
            this.pullingTaskSteps = false;
            return;
        } else if (this.progressCreateSolution === 100 && this.statusCreateSolution === '成功' && this.progressAddArtifact < 100) {
            const len = this.taskSteps.length;
            for (let i = 0; i < len; i++) {
                const taskStep = this.taskSteps[i];
                if (taskStep.stepName === '添加artifact' && taskStep.id > this.lastId) {
                    this.statusAddArtifact = taskStep.stepStatus;
                    this.progressAddArtifact = taskStep.stepProgress;
                    this.detailAddArtifact += (taskStep.description + '\n');
                    this.lastId = taskStep.id;
                }
            }
            this.pullingTaskSteps = false;
            return;
        } else if (this.progressAddArtifact === 100 && this.statusAddArtifact === '成功' && this.progressCreateTosca < 100) {
            const len = this.taskSteps.length;
            for (let i = 0; i < len; i++) {
                const taskStep = this.taskSteps[i];
                if (taskStep.stepName === '生成TOSCA文件' && taskStep.id > this.lastId) {
                    this.statusCreateTosca = taskStep.stepStatus;
                    this.progressCreateTosca = taskStep.stepProgress;
                    this.detailCreateTosca += (taskStep.description + '\n');
                    this.lastId = taskStep.id;
                }
            }
            this.pullingTaskSteps = false;
            return;
        } else if (this.progressCreateTosca === 100 && this.statusCreateTosca === '成功' && this.progressGenerateMicroService < 100) {
            const len = this.taskSteps.length;
            for (let i = 0; i < len; i++) {
                const taskStep = this.taskSteps[i];
                if (taskStep.stepName === '创建微服务' && taskStep.id > this.lastId) {
                    this.statusGenerateMicroService = taskStep.stepStatus;
                    this.progressGenerateMicroService = taskStep.stepProgress;
                    this.detailGenerateMicroService += (taskStep.description + '\n');
                    this.lastId = taskStep.id;
                }
            }
            if (this.progressGenerateMicroService >= 40 && this.progressGenerateMicroService < 69) {
                this.progressGenerateMicroService += 1;
                this.detailGenerateMicroService += '正在创建微服务docker镜像...\n';
                if (this.progressGenerateMicroService === 69) {
                    this.progressGenerateMicroService = 40;
                }
            }
            if (this.progressGenerateMicroService >= 75 && this.progressGenerateMicroService < 87) {
                this.progressGenerateMicroService += 1;
                this.detailGenerateMicroService += '正在推送docker至镜像仓库...\n';
                if (this.progressGenerateMicroService === 87) {
                    this.progressGenerateMicroService = 75;
                }
            }
            this.pullingTaskSteps = false;
            return;
        }
    }

    displayProgressWithPoll() {

        if (this.pullingTaskSteps) {
            return;
        }
        this.pullingTaskSteps = true;

        if (this.getCompleteSuccess() || this.getCompleteFail()) {
            clearInterval(this.timer);
        }

        if (this.progressExtractModelFile < 100) {
            this.ummClient.get_task_steps({
                lastId: this.lastId,
                taskUuid: this.taskUuid,
                stepName: '提取模型文件',
            }).subscribe(
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
            this.ummClient.get_task_steps({
                lastId: this.lastId,
                taskUuid: this.taskUuid,
                stepName: '创建模型对象',
            }).subscribe(
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
            this.ummClient.get_task_steps({
                lastId: this.lastId,
                taskUuid: this.taskUuid,
                stepName: '添加artifact',
            }).subscribe(
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
            this.ummClient.get_task_steps({
                lastId: this.lastId,
                taskUuid: this.taskUuid,
                stepName: '生成TOSCA文件',
            }).subscribe(
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
            this.ummClient.get_task_steps({
                lastId: this.lastId,
                taskUuid: this.taskUuid,
                stepName: '创建微服务',
            }).subscribe(
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

    reloadPage() {
        location.reload();
    }

}
