import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog} from '@angular/material';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/Subscription';
import {ConfirmService, SnackBarService} from '../../shared';
import {Principal, User} from '../../account';
import {AbilityService} from '../index';
import {Ability} from '../model/ability.model';
import {DocumentService, DownloadService} from '../../ucumos';

@Component({
    templateUrl: './ability.component.html',
    styleUrls: ['../ai-ability-datapage.css'],
})
export class AbilityComponent implements OnInit, OnDestroy {

    currentUser: User;
    subscription: Subscription;
    isEditing = false;
    isViewing = false;
    abilityUuid: string;
    ability: Ability = null;
    modelMethod: string;
    requestBody: string;
    responseBody: string;
    sending = false;
    exampleModelMethod: string;
    exampleRequestBody: string;

    constructor(
        private dialog: MatDialog,
        private route: ActivatedRoute,
        private router: Router,
        private principal: Principal,
        private location: Location,
        private abilityService: AbilityService,
        private snackBarService: SnackBarService,
        private confirmService: ConfirmService,
        private documentService: DocumentService,
        private downloadService: DownloadService,
    ) {
    }

    goBack() {
        this.location.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    ngOnInit() {
        this.currentUser = this.principal.getCurrentAccount() ? this.principal.getCurrentAccount() : null;
        this.subscription = this.route.params.subscribe((params) => {
            this.abilityUuid = params['abilityUuid'];
            this.isEditing = params['openMode'] === 'edit';
            this.isViewing = params['openMode'] === 'view';
        });

        this.abilityService.query({
                uuid: this.abilityUuid,
            }).subscribe(
            (res) => {
                this.ability = res.body[0];
                this.getExampleRequest();

                if (this.isEditing  && !this.currentUser.authorities.includes('ROLE_OPERATOR')) {
                    this.snackBarService.error('非正常访问！');
                    this.goBack();
                }
            }, () => {
                this.snackBarService.error('获取模型数据失败！');
                this.goBack();
            }
        );
    }

    viewSolution() {
        this.router.navigate(['/ucumos/solution/' + this.ability.solutionUuid + '/' + 'view']);
    }

    genAbilityUrl(): string {
        return 'POST ' + location.protocol + '//' + location.host + '/ability/model/' + this.ability.uuid + '/{模型方法}';
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
                            const example = JSON.parse(res1.body['text'])['examples'][0];
                            this.exampleModelMethod = example['model-method'];
                            this.exampleRequestBody = JSON.stringify(example['body'], null, 4);
                        }
                    );
                }
            }
        );
    }

    genTestRequest() {
        if (this.exampleModelMethod && this.exampleRequestBody) {
            this.modelMethod = this.exampleModelMethod;
            this.requestBody = this.exampleRequestBody;
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
            })
            .catch((error) => {
                this.responseBody = error;
                this.sending = false;
            });
    }

    updateDemoUrl() {
        this.abilityService.update(this.ability).subscribe();
    }

    stopAbility() {
        this.confirmService.ask('确定要停止运行该实例？').then((confirm) => {
            if (confirm) {
                const oldStatus = this.ability.status;
                this.ability.status = '停止';
                this.abilityService.stop(this.ability).subscribe(
                    (res) => {
                        this.ability = res.body;
                        this.snackBarService.success('成功停止实例运行。');
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

}
