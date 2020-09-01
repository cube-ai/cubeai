import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs/Subscription';
import {Principal} from '../../shared';
import {UmmClient} from '../';
import {TaskStep} from '../model/task-step.model';

@Component({
    templateUrl: './task-onboarding.component.html',
})
export class TaskOnboardingComponent implements OnInit, OnDestroy {

    subscription: Subscription;

    userLogin: string;
    taskUuid: string;
    taskName: string;

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
        private router: Router,
        private route: ActivatedRoute,
        private principal: Principal,
        private ummClient: UmmClient,
    ) {
    }

    ngOnDestroy() {
        clearInterval(this.timer);
        this.subscription.unsubscribe();
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.loadTask();
        });
    }

    loadTask() {
        this.subscription = this.route.params.subscribe((params) => {
            this.taskUuid = params['taskUuid'];
            this.taskName = params['taskName'];
        });

        if (this.progressExtractModelFile === 100) {
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
                    }
                }
            );
        }

        if (this.progressCreateSolution === 100) {
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
                    }
                }
            );
        }

        if (this.progressAddArtifact === 100) {
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
                    }
                }
            );
        }

        if (this.progressCreateTosca === 100) {
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
                    }
                }
            );
        }

        if (this.progressGenerateMicroService === 100) {
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
                    }
                }
            );
        }

        if (!this.getCompleteSuccess() && !this.getCompleteFail()) {
            this.timer = setInterval(() => {
                this.displayProgress();
            }, 1000);
        }
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

}
