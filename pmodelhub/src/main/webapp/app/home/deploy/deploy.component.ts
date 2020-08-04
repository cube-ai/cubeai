import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {ActivatedRoute, Router} from '@angular/router';
import {GlobalService, SnackBarService} from '../../shared';
import {Principal} from '../../shared';
import {HttpClient} from '@angular/common/http';
import {CookieService} from 'ngx-cookie';
import {v4 as uuid} from 'uuid';
import {DeployService, TaskService, TaskStepService, SolutionService, AbilityService, ArtifactService} from '../';
import {Task} from '../model/task.model';
import {TaskStep} from '../model/task-step.model';
import {Solution} from '../model/solution.model';
import {Location} from '@angular/common';

@Component({
    templateUrl: './deploy.component.html',
})
export class DeployComponent implements OnInit, OnDestroy {
    subscription: Subscription;
    userLogin: string;
    isOperator: boolean;
    openMode: string;
    taskUuid: string;
    task: Task;
    solution: Solution;
    dockerImgUrl: string;
    deployTo = 'local';
    deployMode = 'private';
    deployStarted = false;
    progressDeploy = 0;
    detailDeploy = '';
    statusDeploy = '';
    statusCreateTaskError = false;

    timer: any;
    lastId = 0;
    pullingTaskSteps = false;

    constructor(
        private globalService: GlobalService,
        private router: Router,
        private route: ActivatedRoute,
        private location: Location,
        private principal: Principal,
        private http: HttpClient,
        private cookieService: CookieService,
        private snackBarService: SnackBarService,
        private deployService: DeployService,
        private solutionService: SolutionService,
        private artifactService: ArtifactService,
        private abilityService: AbilityService,
        private taskStepService: TaskStepService,
        private taskService: TaskService,
    ) {
    }

    ngOnDestroy() {
        if (this.deployStarted && !this.statusCreateTaskError) {
            clearInterval(this.timer);
            this.snackBarService.info('你已离开[模型部署]页面。之后你还可以在[我的任务]中继续查看任务执行情况...');
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
            this.solutionService.query({
                uuid: tempUuid,
            }).subscribe(
                (res) => {
                    if (res.body && res.body.length > 0) {
                        this.solution = res.body[0];
                        this.deployMode = this.solution.active ? 'public' : 'private';

                        this.taskUuid = uuid().replace(/-/g, '').toLowerCase();

                        this.artifactService.query({
                            solutionUuid: this.solution.uuid,
                            type: 'DOCKER镜像',
                        }).subscribe(
                            (res1) => {
                                if (res1 && res1.body.length > 0) {
                                    this.dockerImgUrl = res1.body[0].url;
                                }
                            }
                        );
                    } else {
                        this.snackBarService.error('未找到模型！');
                    }
                }
            );
        } else if (this.openMode === 'view') {
            this.deployTo = 'cubeai';
            this.taskUuid = tempUuid;
            this.taskService.query({
                uuid: this.taskUuid,
            }).subscribe(
                (res) => {
                    if (res.body && res.body.length > 0) {
                        this.task = res.body[0];
                        this.deployStarted = true;
                        if (!this.getCompleteSuccess() && !this.getCompleteFail()) {
                            this.timer = setInterval(() => {
                                this.displayProgress();
                            }, 1000);
                        }
                    } else {
                        this.snackBarService.error('未找到任务！');
                    }
                }
            );
        }
    }

    genLocalDeployCmd(): string {
        return 'docker run ' + this.dockerImgUrl;
    }

    deploySolution() {
        if (this.deployMode === 'public') {
            this.abilityService.query({
                isPublic: true,
                status: '运行',
                solutionUuid: this.solution.uuid,
            }).subscribe(
                (res) => {
                    if (res && res.body.length > 0) {
                        this.snackBarService.info('CubeAI开放能力平台中已存在该模型的部署实例，请勿重复部署！');
                    } else {
                        this.doDeploySolution();
                    }
                }, () => {
                    this.statusCreateTaskError = true;
                }
            );
        } else if (this.deployMode === 'private') {
            this.abilityService.query({
                isPublic: false,
                status: '运行',
                deployer: this.userLogin,
            }).subscribe(
                (res) => {
                    if (res && res.body.length > 0) {
                        if (res.body.length > 4) {
                            this.snackBarService.info('你部署的私有能力数已达到上限（5个），不能再部署新的实例！');
                        } else {
                            const abilities = res.body.filter((ability) => (ability.solutionUuid === this.solution.uuid));
                            if (abilities.length > 0) {
                                this.snackBarService.info('你的私有能力平台中已存在该模型的部署实例，请勿重复部署！');
                            } else {
                                this.doDeploySolution();
                            }
                        }
                    } else {
                        this.doDeploySolution();
                    }
                }, () => {
                    this.statusCreateTaskError = true;
                }
            );
        }

    }

    doDeploySolution() {
        this.deployService.deploy({
            taskUuid: this.taskUuid,
            solutionUuid: this.solution.uuid,
            public: this.deployMode === 'public',
        }).subscribe(
            () => {
                this.timer = setInterval(() => {
                    this.displayProgress();
                }, 1000);
            }, () => {
                this.statusCreateTaskError = true;
            }
        );
        this.deployStarted = true;
    }

    displayProgress() {

        if (this.pullingTaskSteps) {
            return;
        }
        this.pullingTaskSteps = true;

        if (this.getCompleteSuccess() || this.getCompleteFail()) {
            clearInterval(this.timer);
        }

        if (this.progressDeploy < 100) {
            this.taskStepService.query({
                id: this.lastId,
                taskUuid: this.taskUuid,
                stepName: '模型部署',
            }).subscribe(
                (res) => {
                    const taskSteps: TaskStep[] = res.body;
                    for (let i = 0; i < taskSteps.length; i++) {
                        const taskStep: TaskStep = taskSteps[i];
                        this.statusDeploy = taskStep.stepStatus;
                        this.progressDeploy = taskStep.stepProgress;
                        this.detailDeploy += (taskStep.description + '\n');
                        this.lastId = taskStep.id;
                    }
                    this.pullingTaskSteps = false;
                    return;
                }
            );
        }
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
        this.snackBarService.info('拉取并运行docker镜像的命令已复制到剪贴板...');
    }

    gotoDeployedAbility() {
        window.location.href = '/popen/#/ability/' + this.taskUuid; // 能力部署实例的uuid与部署任务的uuid保持一致
    }

}
