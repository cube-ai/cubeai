import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog} from '@angular/material';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/Subscription';
import {Principal, HttpService, ConfirmService, GlobalService, SnackBarService} from '../../shared';
import {Ability} from '../model/ability.model';
import {Star} from '../model/star.model';
import {DeploymentStatus} from '../model/deployment-status.model';

@Component({
    templateUrl: './ability.component.html',
})
export class AbilityComponent implements OnInit, OnDestroy {
    isMobile = window.screen.width < 960;

    userLogin: string;
    subscription: Subscription;
    isOwner: boolean;
    isOperator: boolean;
    abilityUuid: string;
    ability: Ability = null;
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
        private globalService: GlobalService,
        private dialog: MatDialog,
        private route: ActivatedRoute,
        private router: Router,
        private principal: Principal,
        private location: Location,
        private http: HttpService,
        private snackBarService: SnackBarService,
        private confirmService: ConfirmService,
    ) {
    }

    ngOnDestroy() {
        if (this.isOperator) {
            const body = {
                action: 'update_deployment_admin_info',
                args: {
                    deploymentId: this.ability.id,
                    subject1: this.ability.subject1,
                    subject2: this.ability.subject2,
                    subject3: this.ability.subject3,
                    displayOrder: this.ability.displayOrder,
                },
            };
            this.http.post('umm', body).subscribe();
        }

        this.subscription.unsubscribe();
        clearInterval(this.statusTimer);
    }

    ngOnInit() {
        this.principal.updateCurrentAccount().then(() => {
            this.userLogin = this.principal.getLogin();
            this.isOperator = this.principal.hasAuthority('ROLE_OPERATOR');
            this.subscription = this.route.params.subscribe((params) => {
                this.abilityUuid = params['abilityUuid'];
            });

            const body = {
                action: 'get_deployments',
                args: {
                    uuid: this.abilityUuid,
                },
            };
            this.http.post('umm', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok' && res.body['value']['total'] > 0) {
                        this.ability = res.body['value']['results'][0];
                        this.url_model = location.protocol + '//' + location.host + '/ability/model/' + this.ability.uuid;
                        this.url_web = location.protocol + '//' + location.host + '/ability/web/' + this.ability.uuid + '/';
                        this.isOwner = this.userLogin === this.ability.deployer;
                        this.loadStar();

                        if (this.ability.status === '运行') {
                            this.startGetAbilityStatus();
                        }

                        const body1 = {
                            action: 'get_solutions',
                            args: {
                                uuid: this.ability.solutionUuid,
                            },
                        };
                        this.http.post('umm', body1).subscribe((res1) => {
                            if (res1.body['status'] === 'ok' && res.body['value']['total'] > 0) {
                                this.ability.solution = res1.body['value']['results'][0];
                            }
                        });

                        this.getExampleRequest();
                    } else {
                        this.snackBarService.error('获取能力数据失败！');
                        this.goBack();
                    }
                }, () => {
                    this.snackBarService.error('获取能力数据失败！');
                    this.goBack();
                }
            );

        });
    }

    loadStar() {
        if (this.userLogin) {
            const body = {
                action: 'get_stars',
                args: {
                    userLogin: this.userLogin,
                    targetUuid: this.abilityUuid,
                },
            };
            this.http.post('umm', body).subscribe(
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
            const reason = '你尚未登录，请登录后再关注...';
            const redirectUrl = (window.location.pathname + '@' + this.router.url).replace(/\//g, '$');
            window.location.href = '/#/login/' + redirectUrl + '/' + reason;
        }

        if (this.star) {
            const body = {
                action: 'delete_star',
                args: {
                    starId: this.star.id,
                },
            };
            this.http.post('umm', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.star = null;
                        const body1 = {
                            action: 'update_deployment_star_count',
                            args: {
                                deploymentId: this.ability.id,
                            },
                        };
                        this.http.post('umm', body1).subscribe();
                        this.ability.starCount--;
                    }
                }
            );
        } else {
            const star = new Star();
            star.targetType = 'AI开放能力';
            star.targetUuid = this.abilityUuid;

            const body = {
                action: 'create_star',
                args: {
                    star,
                },
            };
            this.http.post('umm', body).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.loadStar();
                        const body1 = {
                            action: 'update_deployment_star_count',
                            args: {
                                deploymentId: this.ability.id,
                            },
                        };
                        this.http.post('umm', body1).subscribe();
                        this.ability.starCount++;
                    }
                }
            );
        }
    }

    getExampleRequest() {
        const body = {
            action: 'get_documents',
            args: {
                solutionUuid: this.ability.solutionUuid,
                name: 'api-example.txt',
            },
        };
        this.http.post('umm', body).subscribe(
            (res) => {
                if (res.body['status'] === 'ok' && res.body['value'].length > 0) {
                    const url = res.body['value'][0].url;
                    const body1 = {
                        action: 'download_document',
                        args: {
                            url,
                        },
                    };
                    this.http.post('umu', body1).subscribe(
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
            this.snackBarService.error('无可用测试数据！');
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
                this.responseBody = JSON.stringify(JSON.parse(res), null, 4);
                this.sending = false;
                this.ability.callCount ++;
            })
            .catch((error) => {
                this.responseBody = error;
                this.sending = false;
            });
    }

    scaleAbility() {
        const body = {
            action: 'scale_deployment',
            args: {
                deployment: this.ability,
                targetStatus: this.resource,
            },
        };
        this.http.post('umd', body).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.snackBarService.success('扩缩容命令已发出，请等待所有容器副本就绪...');
                } else {
                    this.snackBarService.error('扩缩容操作失败！');
                }
            }, () => {
                this.snackBarService.error('扩缩容命令未发出！');
            }
        );
        this.changing = !this.changing;
    }

    toggleScaleAbility() {
        this.getAbilityStatus();
        this.changing = !this.changing;
    }

    pauseAbility() {
        this.confirmService.ask('确定要暂停该实例？').then((confirm) => {
            if (confirm) {
                const oldStatus = this.ability.status;
                this.ability.status = '正在暂停...';
                this.changing = false;

                const body = {
                    action: 'pause_deployment',
                    args: {
                        deployment: this.ability,
                    },
                };
                this.http.post('umd', body).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.snackBarService.success('停止命令已发出，请等待......');
                            this.ability.status = '暂停';
                        } else {
                            this.snackBarService.error('暂停操作失败！');
                            this.ability.status = oldStatus;
                            this.startGetAbilityStatus();
                        }
                    }, () => {
                        this.snackBarService.error('暂停命令未发出！');
                        this.ability.status = oldStatus;
                        this.startGetAbilityStatus();
                    }
                );
            }
        });
    }

    restartAbility() {
        this.confirmService.ask('确定要重启该实例？').then((confirm) => {
            if (confirm) {
                const oldStatus = this.ability.status;
                this.ability.status = '正在启动...';
                this.changing = false;

                const body = {
                    action: 'restart_deployment',
                    args: {
                        deployment: this.ability,
                    },
                };
                this.http.post('umd', body).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.snackBarService.success('启动命令已发出，请等待所有容器副本就绪...');
                            this.ability.status = '运行';
                            this.startGetAbilityStatus();
                        } else {
                            this.snackBarService.error('启动操作失败！');
                            this.ability.status = oldStatus;
                            this.startGetAbilityStatus();
                        }
                    }, () => {
                        this.snackBarService.error('启动命令未发出！');
                        this.ability.status = oldStatus;
                        this.startGetAbilityStatus();
                    }
                );
            }
        });
    }

    stopAbility() {
        this.confirmService.ask('确定要停止运行该实例？').then((confirm) => {
            if (confirm) {
                const oldStatus = this.ability.status;
                this.ability.status = '正在停止...';

                const body = {
                    action: 'stop_deployment',
                    args: {
                        deployment: this.ability,
                    },
                };
                this.http.post('umd', body).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.snackBarService.success('停止命令已发出，请等待...');
                            this.ability.status = '停止';
                        } else {
                            this.snackBarService.error('停止操作失败！');
                            this.ability.status = oldStatus;
                            this.startGetAbilityStatus();
                        }
                    }, () => {
                        this.snackBarService.error('停止命令未发出！');
                        this.ability.status = oldStatus;
                        this.startGetAbilityStatus();
                    }
                );
            }
        });
    }

    startGetAbilityStatus() {
        if ((this.isOperator || this.isOwner) && !this.isMobile) {
            this.getAbilityStatus();
            this.statusTimer = setInterval(() => {
                this.getAbilityStatus();
            }, 10000);
        }
    }

    getAbilityStatus() {
        if (this.ability.status !== '运行') {
            clearInterval(this.statusTimer);
            return;
        }

        const body = {
            action: 'get_deployment_status',
            args: {
                deploymentUuid: this.abilityUuid,
                username: this.ability.deployer,
            },
        };
        this.http.post('umd', body).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.deploymentStatus = res.body['value'];
                    this.resource = this.deploymentStatus;
                }
            }
        );

        if (this.showPodLogs) {
            const body1 = {
                action: 'get_deployment_logs',
                args: {
                    deploymentUuid: this.abilityUuid,
                    username: this.ability.deployer,
                },
            };
            this.http.post('umd', body1).subscribe(
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
        if (this.ability.solution) {
            window.location.href = '/pmodelhub/#/solution/' + this.ability.solutionUuid;
        } else {
            this.snackBarService.warning('本实例所对应模型已不存在，无法访问！');
        }

    }

    gotoPersonalAbilitys(deployer: string) {
        this.router.navigate(['/personal/' + deployer]);
    }

    gotoPersonalModels(solutionAuthor: string) {
        window.location.href = '/pmodelhub/#/personal/' + solutionAuthor;
    }

    gotoStargazers() {
        if (this.ability.starCount > 0) {
            this.router.navigate(['/stargazer/' + this.ability.uuid + '/' + this.ability.solutionName]);
        }
    }

    goBack() {
        this.location.back();
    }

}
