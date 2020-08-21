import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog} from '@angular/material';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/Subscription';
import {ConfirmService, GlobalService, SnackBarService} from '../../shared';
import {Principal} from '../../shared';
import {AbilityService} from '..';
import {SolutionService} from '..';
import {Ability} from '../model/ability.model';
import {DocumentService, DownloadService} from '../';
import {StarService} from '..';
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
        private abilityService: AbilityService,
        private solutionService: SolutionService,
        private snackBarService: SnackBarService,
        private confirmService: ConfirmService,
        private documentService: DocumentService,
        private downloadService: DownloadService,
        private starService: StarService,
    ) {
    }

    ngOnDestroy() {
        if (this.isOperator) {
            this.abilityService.updateAdminInfo({
                id: this.ability.id,
                subject1: this.ability.subject1,
                subject2: this.ability.subject2,
                subject3: this.ability.subject3,
                displayOrder: this.ability.displayOrder,
            }).subscribe();
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

            this.abilityService.query({
                uuid: this.abilityUuid,
            }).subscribe(
                (res) => {
                    this.ability = res.body[0];
                    this.url_model = location.protocol + '//' + location.host + '/ability/model/' + this.ability.uuid;
                    this.url_web = location.protocol + '//' + location.host + '/ability/web/' + this.ability.uuid + '/';
                    this.isOwner = this.userLogin === this.ability.deployer;
                    this.loadStar();

                    if (this.ability.status === '运行') {
                        this.startGetAbilityStatus();
                    }

                    this.solutionService.query({
                        uuid: this.ability.solutionUuid,
                        active: this.ability.isPublic,
                    }).subscribe((res2) => {
                        if (res2.body.length > 0) {
                            this.ability.solution = res2.body[0];
                        }
                    });

                    this.getExampleRequest();
                }, () => {
                    this.snackBarService.error('获取能力数据失败！');
                    this.goBack();
                }
            );

        });
    }

    loadStar() {
        if (this.userLogin) {
            this.starService.query({
                userLogin: this.userLogin,
                targetUuid: this.abilityUuid,
            }).subscribe(
                (res) => {
                    if (res.body && res.body.length > 0) {
                        this.star = res.body[0];
                    } else {
                        this.star = null;
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
            this.starService.delete(this.star.id).subscribe(() => {
                this.star = null;
                this.ability.starCount--;
                this.abilityService.updateStarCount({
                    id: this.ability.id,
                }).subscribe();
            });
        } else {
            const star = new Star();
            star.targetType = 'AI开放能力';
            star.targetUuid = this.abilityUuid;
            this.starService.create(star).subscribe(() => {
                this.loadStar();
                this.ability.starCount++;
                this.abilityService.updateStarCount({
                    id: this.ability.id,
                }).subscribe();
            });
        }
    }

    getExampleRequest() {
        this.documentService.query({
            solutionUuid: this.ability.solutionUuid,
            name: 'api-example.txt',
        }).subscribe(
            (res) => {
                if (res && res.body.length > 0) {
                    const url = res.body[0].url;
                    this.downloadService.download(url).subscribe(
                        (res1) => {
                            this.examples = JSON.parse(res1.body)['examples'];
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

    updateDemoUrl() {
        this.abilityService.updateDemoUrl({
            id: this.ability.id,
            demoUrl: this.ability.demoUrl,
        }).subscribe();
    }

    changeAbility(lcm) {
        const body = {
            deployment: this.ability,
            resource: this.resource,
            lcm
        };
        if (lcm === 'start') {
            this.confirmService.ask('确定要重新启动该实例？').then((confirm) => {
                if (confirm) {
                    const oldStatus = this.ability.status;
                    this.ability.status = '正在启动...';
                    this.changing = false;
                    this.abilityService.change(body).subscribe(
                        () => {
                            this.snackBarService.success('启动命令已发出，请等待所有容器副本就绪...');
                            this.ability.status = '运行';
                            this.startGetAbilityStatus();
                        }, () => {
                            this.ability.status = oldStatus;
                        }
                    );
                }
            });
        } else if (lcm === 'pause') {
            this.confirmService.ask('确定要暂停该实例？').then((confirm) => {
                if (confirm) {
                    const oldStatus = this.ability.status;
                    this.ability.status = '正在暂停...';
                    this.changing = false;
                    this.abilityService.change(body).subscribe(
                        () => {
                            this.snackBarService.success('暂停命令已发出，请等待...');
                            this.ability.status = '暂停';
                        }, () => {
                            this.ability.status = oldStatus;
                            this.startGetAbilityStatus();
                        }
                    );
                }
            });
        } else {
            if (this.changing && lcm === 'change') {
                this.abilityService.change(body).subscribe(
                    () => {
                        this.snackBarService.success('扩缩容命令已发出，请等待所有容器副本就绪...');
                    }, () => {
                        this.snackBarService.info('扩缩容操作失败！');
                    }
                );
            } else {
                this.getAbilityStatus();
                this.resource = this.deploymentStatus;
            }
            this.changing = !this.changing;
        }
    }

    stopAbility() {
        this.confirmService.ask('确定要停止运行该实例？').then((confirm) => {
            if (confirm) {
                const oldStatus = this.ability.status;
                this.ability.status = '正在停止...';
                this.abilityService.stop(this.ability).subscribe(
                    () => {
                        this.snackBarService.success('停止命令已发出，请等待...');
                        this.ability.status = '停止';
                    }, () => {
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
            }, 5000);
        }
    }

    getAbilityStatus() {
        if (this.ability.status !== '运行') {
            clearInterval(this.statusTimer);
        }
        this.abilityService.status({
            uuid: this.abilityUuid,
            user: this.ability.deployer,
        }).subscribe(
            (res) => {
                this.deploymentStatus = res.body;
            }
        );
        if (this.showPodLogs) {
            this.abilityService.logs({
                uuid: this.abilityUuid,
                user: this.ability.deployer,
            }).subscribe(
                (res) => {
                    this.podLogs = res.body.logs;
                    console.log(this.podLogs);
                }
            );
        }
    }

    togglePodLogs() {
        if (!this.showPodLogs) {
            this.getAbilityStatus();
        }
        this.showPodLogs = !this.showPodLogs;
    }

    openDemoUrl() {
        const link: HTMLElement = document.createElement('a');
        link.setAttribute('href', this.ability.demoUrl);
        link.setAttribute('target', '_blank');
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
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
