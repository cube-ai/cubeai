import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs';
import {Principal} from '../../shared';
import {UmmClient} from '../service/umm_client.service';
import {UmuClient} from '../service/umu_client.service';
import {UmdClient} from '../service/umd_client.service';
import {Solution} from '../model/solution.model';
import {Star} from '../model/star.model';
import {DeploymentStatus} from '../model/deployment-status.model';
import {MessageService} from 'primeng/api';
import {ConfirmationService} from 'primeng/api';

@Component({
    templateUrl: './ability.component.html',
})
export class AbilityComponent implements OnInit, OnDestroy {
    userLogin: string;
    subscription: Subscription;
    isOwner: boolean;
    isOperator: boolean;
    solutionUuid: string;
    solution: Solution = null;
    url_model = '';
    url_web = '';
    requestBody: string;
    responseBody: string;
    sending = false;
    examples: any[];
    star: Star;
    statusTimer: any;
    deploymentStatus: DeploymentStatus = null;
    resource: DeploymentStatus = null;
    changing = false;
    podLogs = null;
    showPodLogs = false;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private principal: Principal,
        private location: Location,
        private ummClient: UmmClient,
        private umuClient: UmuClient,
        private umdClient: UmdClient,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
    ) {
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        clearInterval(this.statusTimer);
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.isOperator = this.principal.hasAuthority('ROLE_OPERATOR');
            this.subscription = this.route.params.subscribe((params) => {
                this.solutionUuid = params['solutionUuid'];
            });

            this.ummClient.get_solutions({
                uuid: this.solutionUuid,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok' && res.body['value']['total'] > 0) {
                        this.solution = res.body['value']['results'][0];
                        this.url_model = location.protocol + '//' + location.host + '/ability/model/' + this.solution.uuid;
                        this.url_web = location.protocol + '//' + location.host + '/ability/web/' + this.solution.uuid + '/';
                        this.isOwner = this.userLogin === this.solution.deployer;
                        this.loadStar();

                        if (this.solution.deployStatus === '运行') {
                            this.startGetAbilityStatus();
                        }

                        this.getExampleRequest();
                    } else {
                        this.messageService.add({severity:'error', detail:'获取能力数据失败！'});
                        this.goBack();
                    }
                }, () => {
                    this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
                    this.goBack();
                }
            );

        });
    }

    loadStar() {
        if (this.userLogin) {
            this.ummClient.get_stars({
                userLogin: this.userLogin,
                targetUuid: this.solutionUuid,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        if (res.body['value']['total'] > 0) {
                            this.star = res.body['value']['results'][0];
                        } else {
                            this.star = null;
                        }
                    }
                }
            );
        }

    }

    toggleStar() {
        if (!this.userLogin) {
            window.localStorage.setItem('loginReason', '你尚未登录，请登录后再关注...');
            window.localStorage.setItem('loginRedirectUrl', window.location.pathname + '#' + this.router.url);
            window.location.href = '/#/login';
        }

        if (this.star) {
            this.ummClient.delete_star({
                starId: this.star.id,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.star = null;
                        this.ummClient.update_solution_star_count({
                            solutionId: this.solution.id,
                        }).subscribe();
                        this.solution.starCount--;
                    }
                }
            );
        } else {
            const star = new Star();
            star.targetUuid = this.solutionUuid;
            this.ummClient.create_star({
                star,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.loadStar();
                        this.ummClient.update_solution_star_count({
                            solutionId: this.solution.id,
                        }).subscribe();
                        this.solution.starCount++;
                    }
                }
            );
        }
    }

    getExampleRequest() {
        this.ummClient.get_documents({
            solutionUuid: this.solution.uuid,
            name: 'api-example.txt',
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok' && res.body['value'].length > 0) {
                    this.umuClient.download_document({
                        url: res.body['value'][0].url,
                    }).subscribe(
                        (res1) => {
                            this.examples = JSON.parse(res1.body['value'])['examples'];
                        }
                    );
                }
            }
        );
    }

    genTestRequest() {
        if (this.examples) {
            const index = Math.floor(Math.random() * this.examples.length);
            const example = this.examples[index]; // 从所有例子中随机取一个
            this.requestBody = JSON.stringify(example, null, 4);
        } else {
            this.messageService.add({severity:'error', detail:'无可用测试数据！'});
        }
    }

    cleanTestRequest() {
        this.requestBody = null;
    }

    cleanResponseBody() {
        this.responseBody = null;
    }

    sendTestRequest() {
        this.sending = true;
        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: this.requestBody,
            method: 'POST',
        };

        fetch(this.url_model, params).then((data) => data.text())
            .then((res) => {
                if (res.startsWith('{')) {
                    this.responseBody = JSON.stringify(JSON.parse(res), null, 4);
                } else {
                    this.responseBody = res;
                }
                this.sending = false;
                this.solution.callCount ++;
            })
            .catch((error) => {
                this.responseBody = error;
                this.sending = false;
            });
    }

    scaleAbility() {
        this.umdClient.scale_deployment({
            solution: this.solution,
            targetStatus: this.resource,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.messageService.add({severity:'success', detail:'扩缩容命令已发出，请等待所有容器副本就绪...'});
                } else {
                    this.messageService.add({severity:'error', detail:'扩缩容操作失败！'});
                }
            }, () => {
                this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
            }
        );
        this.changing = !this.changing;
    }

    toggleScaleAbility() {
        this.getAbilityStatus();
        this.changing = !this.changing;
    }

    pauseAbility() {
        this.confirmationService.confirm({
            target: event.target,
            message: '确定要暂停该实例？',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: '是',
            rejectLabel: '否',
            accept: () => {
                const oldStatus = this.solution.deployStatus;
                this.solution.deployStatus = '正在暂停...';
                this.changing = false;

                this.umdClient.pause_deployment({
                    solution: this.solution,
                }).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.messageService.add({severity:'success', detail:'停止命令已发出，请等待......'});
                            this.solution.deployStatus = '暂停';
                        } else {
                            this.messageService.add({severity:'error', detail:'暂停操作失败！'});
                            this.solution.deployStatus = oldStatus;
                            this.startGetAbilityStatus();
                        }
                    }, () => {
                        this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
                        this.solution.deployStatus = oldStatus;
                        this.startGetAbilityStatus();
                    }
                );
            },
            reject: () => {}
        });
    }

    restartAbility() {
        this.confirmationService.confirm({
            target: event.target,
            message: '确定要重启该实例？',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: '是',
            rejectLabel: '否',
            accept: () => {
                const oldStatus = this.solution.deployStatus;
                this.solution.deployStatus = '正在启动...';
                this.changing = false;

                this.umdClient.restart_deployment({
                    solution: this.solution,
                }).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.messageService.add({severity:'success', detail:'启动命令已发出，请等待所有容器副本就绪...'});
                            this.solution.deployStatus = '运行';
                            this.startGetAbilityStatus();
                        } else {
                            this.messageService.add({severity:'error', detail:'启动操作失败！'});
                            this.solution.deployStatus = oldStatus;
                            this.startGetAbilityStatus();
                        }
                    }, () => {
                        this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
                        this.solution.deployStatus = oldStatus;
                        this.startGetAbilityStatus();
                    }
                );
            },
            reject: () => {}
        });
    }

    stopAbility() {
        this.confirmationService.confirm({
            target: event.target,
            message: '确定要停止运行该实例？',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: '是',
            rejectLabel: '否',
            accept: () => {
                const oldStatus = this.solution.deployStatus;
                this.solution.deployStatus = '正在停止...';

                this.umdClient.stop_deployment({
                    solution: this.solution,
                }).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.messageService.add({severity:'success', detail:'停止命令已发出，请等待...'});
                            this.solution.deployStatus = '停止';
                        } else {
                            this.messageService.add({severity:'error', detail:'停止操作失败！'});
                            this.solution.deployStatus = oldStatus;
                            this.startGetAbilityStatus();
                        }
                    }, () => {
                        this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
                        this.solution.deployStatus = oldStatus;
                        this.startGetAbilityStatus();
                    }
                );
            },
            reject: () => {}
        });
    }

    startGetAbilityStatus() {
        if (this.isOperator || this.isOwner) {
            this.getAbilityStatus();
            this.statusTimer = setInterval(() => {
                this.getAbilityStatus();
            }, 10000);
        }
    }

    getAbilityStatus() {
        if (this.solution.deployStatus !== '运行') {
            clearInterval(this.statusTimer);
            return;
        }

        this.umdClient.get_deployment_status({
            solutionUuid: this.solutionUuid,
            username: this.solution.deployer,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.deploymentStatus = res.body['value'];
                    this.resource = this.deploymentStatus;
                }
            }
        );

        if (this.showPodLogs) {
            this.umdClient.get_deployment_logs({
                solutionUuid: this.solutionUuid,
                username: this.solution.deployer,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.podLogs = res.body['value'];
                    }
                }
            );
        }
    }

    togglePodLogs() {
        this.showPodLogs = !this.showPodLogs;
        if (this.showPodLogs) {
            this.getAbilityStatus();
        }
    }

    openWeb() {
        const link: HTMLElement = document.createElement('a');
        link.setAttribute('href', this.url_web);
        link.setAttribute('target', '_blank');
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    gotoSolution() {
        window.location.href = '/pmodelhub/#/solution/' + this.solutionUuid;
    }

    gotoPersonalAbilitys(deployer: string) {
        this.router.navigate(['/personal/' + deployer]);
    }

    gotoPersonalModels(solutionAuthor: string) {
        window.location.href = '/pmodelhub/#/personal/' + solutionAuthor;
    }

    gotoStargazers() {
        if (this.solution.starCount > 0) {
            this.router.navigate(['/stargazer/' + this.solution.uuid + '/' + this.solution.name]);
        }
    }

    goBack() {
        this.location.back();
    }

}
