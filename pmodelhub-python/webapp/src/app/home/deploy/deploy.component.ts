import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {Principal} from '../../shared';
import {UmmClient} from '../service/umm_client.service';
import {UmdClient} from '../service/umd_client.service';
import {v4 as uuid} from 'uuid';
import {Task} from '../model/task.model';
import {TaskStep} from '../model/task-step.model';
import {Solution} from '../model/solution.model';
import {Location} from '@angular/common';
import {MessageService} from 'primeng/api';

@Component({
    templateUrl: './deploy.component.html',
})
export class DeployComponent implements OnInit, OnDestroy {
    subscription: Subscription;
    userLogin: string;
    isOperator: boolean;
    openMode: string;
    taskUuid: string;

    websocket: WebSocket;
    taskSteps: TaskStep[] = [];

    task: Task;
    solution: Solution;
    solutionUuid: string;
    dockerImgUrl: string;
    deployTo = 'local';
    deployMode = 'private';
    deployStarted = false;
    progressDeploy = 0;
    detailDeploy = '';
    viewDetailDeploy = true;
    statusDeploy = '';
    statusCreateTaskError = false;

    timer: any;
    lastId = 0;
    pullingTaskSteps = false;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private location: Location,
        private principal: Principal,
        private ummClient: UmmClient,
        private umdClient: UmdClient,
        private messageService: MessageService,
    ) {
    }

    ngOnDestroy() {
        if (this.deployStarted && !this.statusCreateTaskError) {
            clearInterval(this.timer);
            this.websocket.close();
            this.messageService.add({severity:'info', detail:'你已离开[模型部署]页面。之后你还可以在[我的任务]中继续查看任务执行情况...'});
        }
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.isOperator = this.principal.getAuthorities().includes('ROLE_OPERATOR');
            this.doDeploy();
        });

    }

    doDeploy() {
        let tempUuid = null;
        this.subscription = this.route.params.subscribe((params) => {
            this.openMode = params['openMode'];
            tempUuid = params['uuid'];
        });

        if (this.openMode === 'deploy') {
            this.solutionUuid = tempUuid;
            this.ummClient.get_solutions({
                'uuid': this.solutionUuid,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok' && res.body['value']['total'] > 0) {
                        this.solution = res.body['value']['results'][0];
                        this.deployMode = this.solution.active ? 'public' : 'private';
                        this.ummClient.get_artifacts({
                            solutionUuid: this.solution.uuid,
                            type: 'DOCKER镜像',
                        }).subscribe(
                            (res1) => {
                                if (res1.body['status'] === 'ok' && res1.body['value'].length > 0) {
                                    this.dockerImgUrl = res1.body['value'][0].url;
                                }
                            }
                        );
                    } else {
                        this.messageService.add({severity:'error', detail:'未找到模型！'});
                    }
                }
            );
        } else if (this.openMode === 'view') {
            this.deployTo = 'cubeai';
            this.taskUuid = tempUuid;
            this.ummClient.get_tasks({
                    uuid: this.taskUuid,
                }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok' && res.body['value']['total'] > 0) {
                        this.task = res.body['value']['results'][0];
                        this.solutionUuid = this.task.targetUuid;
                        this.deployStarted = true;
                        if (!this.getCompleteSuccess() && !this.getCompleteFail()) {
                            this.loadProgressfromDb();
                            this.timer = setInterval(() => {
                                this.displayProgressWithWebsocket();
                            }, 1000);
                            this.connectWebSocket();
                        }
                    } else {
                        this.messageService.add({severity:'error', detail:'未找到任务！'});
                    }
                }
            );
        }
    }

    genLocalDeployCmd(): string {
        return 'docker run ' + this.dockerImgUrl;
    }

    deploySolution() {
        if (this.solution.deployStatus !== '停止') {
            this.messageService.add({severity:'warn', detail:'模型已部署，请勿重复部署！'});
        } else {
            this.doDeploySolution();
        }
    }

    doDeploySolution() {
        this.umdClient.deploy_model({
            solutionUuid: this.solution.uuid,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.taskUuid = res.body['value'];
                    this.timer = setInterval(() => {
                        this.displayProgressWithWebsocket();
                    }, 1000);
                    this.connectWebSocket();
                } else {
                    this.statusCreateTaskError = true;
                }
            }, () => {
                this.statusCreateTaskError = true;
            }
        );
        this.deployStarted = true;
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

        if (this.progressDeploy < 100) {
            const len = this.taskSteps.length;
            for (let i = 0; i < len; i++) {
                const taskStep = this.taskSteps[i];
                if (taskStep.stepName === '模型部署' && taskStep.id > this.lastId ) {
                    this.statusDeploy = taskStep.stepStatus;
                    this.progressDeploy  = taskStep.stepProgress;
                    this.detailDeploy  += (taskStep.description + '\n');
                    this.lastId = taskStep.id;
                }
            }
            if (this.progressDeploy >= 40 && this.progressDeploy < 79) {
                this.progressDeploy += 1;
                this.detailDeploy += '正在创建Kubernetes部署对象...\n';
                if (this.progressDeploy === 79) {
                    this.progressDeploy = 40;
                }
            }
            this.pullingTaskSteps = false;
            return;
        }
    }

    loadProgressfromDb() {
        if (this.progressDeploy < 100) {
            this.ummClient.get_task_steps({
                lastId: this.lastId,
                taskUuid: this.taskUuid,
                stepName: '模型部署',
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        const taskSteps: TaskStep[] = res.body['value'];
                        for (let i = 0; i < taskSteps.length; i++) {
                            const taskStep: TaskStep = taskSteps[i];
                            this.statusDeploy = taskStep.stepStatus;
                            this.progressDeploy = taskStep.stepProgress;
                            this.detailDeploy += (taskStep.description + '\n');
                            this.lastId = taskStep.id;
                        }
                        if (this.progressDeploy >= 40 && this.progressDeploy < 79) {
                            this.progressDeploy += 1;
                            this.detailDeploy += '正在创建Kubernetes部署对象...\n';
                            if (this.progressDeploy === 79) {
                                this.progressDeploy = 40;
                            }
                        }
                    }
                }
            );
        }
    }

    toggleViewDetailDeploy() {
        this.viewDetailDeploy = !this.viewDetailDeploy;
    }

    getCompleteSuccess(): boolean {
        return this.statusDeploy === '成功';
    }

    getCompleteFail(): boolean {
        return this.statusDeploy === '失败';
    }

    copyDockerUrl(dockerUrl: string) {
        const oInput = document.createElement('input');
        oInput.value = 'docker run ' + dockerUrl;
        document.body.appendChild(oInput);
        oInput.select();
        document.execCommand('Copy'); // 执行浏览器复制命令
        oInput.className = 'oInput';
        oInput.style.display = 'none';
        this.messageService.add({severity:'info', detail:'已复制拉取并运行docker镜像的命令到剪贴板...'});
    }

    gotoDeployedAbility() {
        window.location.href = '/popen/#/ability/' + this.solutionUuid;
    }

}
