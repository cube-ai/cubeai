import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog} from '@angular/material';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/Subscription';
import {ConfirmService, GlobalService, SnackBarService} from '../../shared';
import {Principal} from '../../shared';
import {AbilityService} from '../index';
import {SolutionService} from '../service/solution.service';
import {Ability} from '../model/ability.model';
import {DocumentService, DownloadService} from '../';
import {StarService} from '../service/star.service';
import {Star} from '../model/star.model';

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
    modelMethod: string;
    requestBody: string;
    responseBody: string;
    sending = false;
    examples: any[];
    star: Star;

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
                    this.isOwner = this.userLogin === this.ability.deployer;
                    this.loadStar();

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

    genAbilityUrl(): string {
        let methods = '';
        if (this.examples && this.examples.length > 0) {
            methods = ': ';
            for (let i = 0; i < this.examples.length; i++) {
                methods += this.examples[i]['model-method'];
                if (i < this.examples.length - 1) {
                    methods += ' | ';
                }
            }
        }
        return 'POST ' + location.protocol + '//' + location.host + '/ability/model/' + this.ability.uuid + '/{模型方法' + methods + '}';
    }

    genAbilityUrlPrefix(): string {
        return location.protocol + '//' + location.host + '/ability/model/' + this.ability.uuid + '/';
    }

    getExampleRequest() {
        this.documentService.query({
            solutionUuid: this.ability.solutionUuid,
            name: 'api-example.txt',
        }).subscribe(
            (res) => {
                if (res && res.body.length > 0) {
                    const url = res.body[0].url;
                    this.downloadService.getFileText(url).subscribe(
                        (res1) => {
                            this.examples = JSON.parse(res1.body['text'])['examples'];
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
            this.modelMethod = example['model-method'];
            this.requestBody = JSON.stringify(example['body'], null, 4);
        } else {
            this.snackBarService.error('无可用测试数据！');
        }
    }

    cleanTestRequest() {
        this.modelMethod = null;
        this.requestBody = null;
    }

    cleanResponseBody() {
        this.responseBody = null;
    }

    sendTestRequest() {
        this.sending = true;
        const url = this.genAbilityUrlPrefix() + this.modelMethod;
        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: this.requestBody,
            method: 'POST',
        };

        fetch(url, params).then((data) => data.text())
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

    stopAbility() {
        this.confirmService.ask('确定要停止运行该实例？').then((confirm) => {
            if (confirm) {
                const oldStatus = this.ability.status;
                this.ability.status = '停止';
                this.abilityService.stop(this.ability).subscribe(
                    () => {
                        this.snackBarService.success('停止命令已发出，请稍后刷新能力列表查看结果...');
                        this.ability.status = '正在停止...';
                    }, () => {
                        this.ability.status = oldStatus;
                    }
                );
            }
        });
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
