import {Component, OnInit, OnDestroy} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '@angular/common';
import {Subscription} from 'rxjs';
import {SERVER_API_URL} from '../../app.constants';
import {FileUploader} from 'ng2-file-upload';
import {FileItem} from 'ng2-file-upload/file-upload/file-item.class';
import {Principal, UaaClient, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {UmmClient} from '../service/umm_client.service';
import {UmuClient} from '../service/umu_client.service';
import {Solution} from '../model/solution.model';
import {Artifact} from '../model/artifact.model';
import {Document} from '../model/document.model';
import {Description} from '../model/description.model';
import {Comment} from '../model/comment.model';
import {Star} from '../model/star.model';
import {v4 as uuid} from 'uuid';
import {LazyLoadEvent} from 'primeng/api';
import {MessageService} from 'primeng/api';
import {ConfirmationService} from 'primeng/api';
import {ImageCroppedEvent} from "ngx-image-cropper";

@Component({
    templateUrl: './solution.component.html',
})
export class SolutionComponent implements OnInit, OnDestroy {

    userLogin: string;
    subscription: Subscription;
    solutionUuid: string;
    isEditing = false;
    isOwner = false;
    isManager = false;
    isOperator = false;
    readyToDelete = false;
    deleteConfirmText: string;
    solution: Solution = null;
    description: Description = new Description();
    ymlText: string;
    artifacts: Artifact[];
    documents: Document[];
    uploader: FileUploader;
    imgSelector: FileUploader;
    star: Star;
    comments: Comment[] = [];
    commentText: string;
    inputPictureUrl: string;

    // 用于Comment的分页显示
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 0;

    modelTypes = [
        {'label': '自然语言处理', 'value': '自然语言处理'},
        {'label': '计算机视觉', 'value': '计算机视觉'},
        {'label': '语音语义', 'value': '语音语义'},
        {'label': '推荐算法', 'value': '推荐算法'},
        {'label': '知识图谱', 'value': '知识图谱'},
        {'label': '强化学习', 'value': '强化学习'},
        {'label': '机器学习', 'value': '机器学习'},
        {'label': '深度学习', 'value': '深度学习'},
        {'label': '联邦学习', 'value': '联邦学习'},
        {'label': '量子计算', 'value': '量子计算'},
        {'label': '网络通信', 'value': '网络通信'},
        {'label': '自动驾驶', 'value': '自动驾驶'},
        {'label': '地理测绘', 'value': '地理测绘'},
        {'label': '智慧医疗', 'value': '智慧医疗'},
        {'label': '机器人', 'value': '机器人'},
        {'label': '大数据', 'value': '大数据'},
        {'label': '可视化', 'value': '可视化'},
        {'label': '预测', 'value': '预测'},
        {'label': '分类', 'value': '分类'},
        {'label': '回归', 'value': '回归'},
        {'label': '聚类', 'value': '聚类'},
        {'label': '降维', 'value': '降维'},
        {'label': '其他', 'value': '其他'},
    ];
    modelToolkits = [
        {'label': 'PaddlePaddle', 'value': 'PaddlePaddle'},
        {'label': 'TensorFlow', 'value': 'TensorFlow'},
        {'label': 'PyTorch', 'value': 'PyTorch'},
        {'label': 'Scikit-Learn', 'value': 'Scikit-Learn'},
        {'label': 'Keras', 'value': 'Keras'},
        {'label': 'Python', 'value': 'Python'},
        {'label': 'C/C++', 'value': 'C/C++'},
        {'label': '其他', 'value': '其他'},
    ];

    selectedEditMode = 'markdown';
    editModes = [
        {label: 'mkdown', value: 'markdown'},
        {label: 'HTML', value: 'html'},
    ];

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private principal: Principal,
        private location: Location,
        private uaaClient: UaaClient,
        private ummClient: UmmClient,
        private umuClient: UmuClient,
        private messageService: MessageService,
        private confirmationService: ConfirmationService,
    ) {
    }

    goBack() {
        this.location.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.solutionUuid = params['solutionUuid'];
            this.principal.updateCurrentAccount().then(() => {
                this.userLogin = this.principal.getLogin();
                this.isManager = this.principal.hasAuthority('ROLE_MANAGER');
                this.isOperator = this.principal.hasAuthority('ROLE_OPERATOR');
                this.loadAll();
            });
        });

        this.imgSelector = new FileUploader({});
        this.imgSelector.onAfterAddingFile = (fileItem) => {
            if (!fileItem.file.type.startsWith('image')) {
                this.messageService.add({severity:'error', detail:'文件类型必须是图片！'});
                fileItem.remove();
                return;
            }

            if (this.imgSelector.queue.length > 1) {
                this.imgSelector.queue[0].remove();
            }

            this.readImgFile(fileItem._file);
        };
    }

    readImgFile(file: File) {
        const fileReader = new FileReader();
        fileReader.readAsDataURL(file);
        fileReader.onload = () => {
            this.inputPictureUrl = fileReader.result as string;
        };
    }

    saveCroppedImage(event: ImageCroppedEvent) {
        this.solution.pictureUrl = event.base64;
    }

    getRandomPicture() {
        if (Math.round(Math.random())) {
            this.uaaClient.get_random_picture({
                width: 200,
                height: 200,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.inputPictureUrl = res.body['value'];
                    }
                }
            );
        } else {
            this.uaaClient.get_random_avatar({
                size: 200,
            }).subscribe(
                (res) => {
                    if (res.body['status'] === 'ok') {
                        this.inputPictureUrl = res.body['value'];
                    }
                }
            );
        }
    }

    loadAll() {
        this.ummClient.get_solutions({
            uuid: this.solutionUuid,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    if (res.body['value']['total'] < 1) {
                        this.messageService.add({severity:'error', detail:'模型不存在！'});
                        this.solution = null;
                        return;
                    }

                    this.solution = res.body['value']['results'][0];
                    this.isOwner = this.userLogin === this.solution.authorLogin;

                    if (!this.solution.active && !this.isOwner && !this.isManager) {
                        this.messageService.add({severity:'error', detail:'模型已被作者设为私有，无法继续访问！'});
                        this.solution = null;
                        return;
                    }

                    if (!this.solution.pictureUrl) {
                            this.uaaClient.get_random_avatar({
                                size: 200,
                            }).subscribe(
                                (res1) => {
                                    if (res1.body['status'] === 'ok') {
                                        this.solution.pictureUrl = res1.body['value'];
                                        this.ummClient.update_solution_picture_url({
                                            solutionId: this.solution.id,
                                            pictureUrl: this.solution.pictureUrl,
                                        }).subscribe();
                                    }
                                }
                            );
                    }

                    this.ummClient.update_solution_view_count({
                        solutionId: this.solution.id,
                    }).subscribe();
                    this.solution.viewCount ++;

                    this.loadDescription();
                    this.loadArtifactData();
                    this.loadDocumentData();
                    this.loadStar();

                    this.uploader = new FileUploader({
                        url: SERVER_API_URL + 'umu/api/file/upload_document/' + this.solution.uuid,
                        method: 'POST',
                        itemAlias: 'upload_document',
                    });
                } else {
                    this.messageService.add({severity:'error', detail:'获取模型数据失败！'});
                    this.solution = null;
                    return;
                }
            }, () => {
                this.messageService.add({severity:'error', detail:'网络或服务器故障！'});
                this.solution = null;
                return;
            }
        );
    }

    canEdit(): boolean {
        return this.isOwner || this.isManager;
    }

    upload(fileItem: FileItem) {
        fileItem.onSuccess = () => {
            this.loadDocumentData();
        };

        fileItem.onError = () => {
            this.loadDocumentData();
        };

        if (fileItem.file.size > 20 * 1024 * 1024) {
            this.messageService.add({severity:'error', detail:'上传文档不能大于20MB！'});
            fileItem.remove();
            return;
        }

        if (this.documents.length > 4) {
            this.messageService.add({severity:'error', detail:'一个模型最多5个文档！请删除现有文档后再上传新文档...'});
            return;
        }

        if (this.findExistDocument(fileItem.file.name)) {
            this.messageService.add({severity:'error', detail:'本模型中已存在同名文档！请删除现有文档后再上传新文档...'});
        } else {
            fileItem.upload();
        }
    }

    findExistDocument(fileName: string): boolean {
        for (let i = 0; i < this.documents.length; i++) {
            if (this.documents[i].name === fileName) {
                return true;
            }
        }
        return false;
    }

    loadDescription() {
        this.ummClient.find_description({
            solutionUuid: this.solution.uuid,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.description = res.body['value'];
                    this.selectedEditMode = this.description.content.startsWith('<') ? 'html' : 'markdown';
                }
            }
        );
    }

    loadArtifactData() {
        this.ummClient.get_artifacts({
            solutionUuid: this.solution.uuid,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.artifacts = res.body['value'];
                    this.artifacts.forEach((artifact) => {
                        if (artifact.type === '模型配置') {
                            this.umuClient.download_document({
                                url: artifact.url,
                            }).subscribe(
                                (res1) => {
                                    if (res1.body['status'] === 'ok') {
                                        this.ymlText = res1.body['value'];
                                    }
                                }
                            );
                        }
                    });
                }
            }
        );
    }

    loadDocumentData() {
        this.ummClient.get_documents({
            solutionUuid: this.solution.uuid,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.documents = res.body['value'];
                }
            }
        );
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

    isTextFile(url: string): boolean {
       const ext = url.substring(url.lastIndexOf('.') + 1);
       return ext === 'json' || ext === 'proto' || ext === 'txt' || ext === 'yml' || ext === 'yaml';
    }

    // 将超链接地址隐藏在程序中，不在html中暴露。但实际上在浏览器中还是可以获得该地址的。
    downloadFile(url: string) {
        const link: HTMLElement = document.createElement('a');
        link.setAttribute('href', url);
        link.setAttribute('download', '');
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        this.ummClient.update_solution_download_count({
            solutionId: this.solution.id,
        }).subscribe();
    }

    viewFile(url: string) {
        const link: HTMLElement = document.createElement('a');
        link.setAttribute('href', url);
        link.setAttribute('target', '_blank');
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        this.ummClient.update_solution_download_count({
            solutionId: this.solution.id,
        }).subscribe();
    }

    deleteDocument(document: Document) {
        this.confirmationService.confirm({
            target: event.target,
            message: '确定要删除该文档？',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: '是',
            rejectLabel: '否',
            accept: () => {
                this.umuClient.delete_document({
                    documentId: document.id,
                }).subscribe(
                    () => {
                        this.loadDocumentData();
                    }
                );
            },
            reject: () => {}
        });
    }

    copyDockerUrl(dockerUrl: string) {
        const oInput = document.createElement('input');
        oInput.value = 'docker run ' + dockerUrl;
        document.body.appendChild(oInput);
        oInput.select();
        document.execCommand('Copy'); // 执行浏览器复制命令
        oInput.className = 'oInput';
        oInput.style.display = 'none';
        this.messageService.add({severity:'info', detail:'拉取并运行docker镜像的命令已复制到剪贴板...'});

        this.ummClient.update_solution_download_count({
            solutionId: this.solution.id,
        }).subscribe();
    }

    enterEdit() {
        this.isEditing = true;
    }

    saveAndQuitEdit() {
        this.ummClient.update_solution_baseinfo({
            solutionId: this.solution.id,
            name: this.solution.name,
            company: this.solution.company,
            version: this.solution.version,
            summary: this.solution.summary,
            tag1: this.solution.tag1,
            tag2: this.solution.tag2,
            tag3: this.solution.tag3,
            modelType: this.solution.modelType,
            toolkitType: this.solution.toolkitType,
        }).subscribe((res) => {
            if (res.body['status'] === 'ok') {
                if (this.isManager) {
                    this.ummClient.update_solution_admininfo({
                        solutionId: this.solution.id,
                        subject1: this.solution.subject1,
                        subject2: this.solution.subject2,
                        subject3: this.solution.subject3,
                        displayOrder: this.solution.displayOrder,
                    }).subscribe();
                }
            }
        });

        this.ummClient.update_description({
            descriptionId: this.description.id,
            content: this.description.content,
        }).subscribe();

        this.ummClient.update_solution_picture_url({
            solutionId: this.solution.id,
            pictureUrl: this.solution.pictureUrl,
        }).subscribe();

        this.inputPictureUrl = null;
        this.isEditing = false;
    }

    setSolutionPublic(event, isPublic: boolean) {
        if (!isPublic) {
            this.ummClient.get_my_credit({}).subscribe((res) => {
                if (res.body['status'] === 'ok') {
                    const credit = res.body['value'].credit;
                    if (credit < 20) {
                        this.messageService.add({severity:'error', detail:'模型设为私有需要消耗20积分，你目前仅有' + credit + '积分，无法操作！'});
                        return;
                    } else {
                        this.confirmationService.confirm({
                            target: event.target,
                            message: '模型设为私有将消耗20积分，你现有' + credit + '积分，是否继续？',
                            icon: 'pi pi-exclamation-triangle',
                            acceptLabel: '是',
                            rejectLabel: '否',
                            accept: () => {
                                this.solution.active = isPublic;
                                this.ummClient.update_solution_active({
                                    solutionId: this.solution.id,
                                    active: isPublic,
                                }).subscribe();
                            },
                            reject: () => {}
                        });
                    }
                }
            });
        } else {
            this.solution.active = isPublic;
            this.ummClient.update_solution_active({
                solutionId: this.solution.id,
                active: isPublic,
            }).subscribe();
        }
    }

    deleteSolution() {
        if (this.readyToDelete && this.deleteConfirmText === this.solution.name) {
            this.confirmationService.confirm({
                target: event.target,
                message: '确定要删除模型：' + this.solution.name + '？',
                icon: 'pi pi-exclamation-triangle',
                acceptLabel: '是',
                rejectLabel: '否',
                accept: () => {
                    this.ummClient.delete_solution({
                        solutionId: this.solution.id,
                    }).subscribe((res) => {
                        if (res.body['status'] === 'ok') {
                            this.messageService.add({severity:'success', detail:'成功删除模型...'});
                            this.goBack();
                        } else {
                            this.messageService.add({severity:'error', detail:'删除模型失败！'});
                        }
                    });
                },
                reject: () => {}
            });
        } else {
            this.messageService.add({severity:'error', detail:'确认码不正确，模型未删除...'});
        }
    }

    starSolution() {
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

    loadComments() {
        this.ummClient.get_comments({
            solutionUuid: this.solution.uuid,
            parentUuid: '0',
            page: this.page,
            size: this.itemsPerPage,
            sort: ['id,desc'],
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.totalItems = res.body['value']['total'];
                    this.comments = res.body['value']['results'];
                    this.comments.forEach(
                        (comment) => {
                            this.loadReplyComments(comment);
                        }
                    );
                }
            }
        );
    }

    reloadPage(event: LazyLoadEvent) {
        if (event.rows) {
            this.itemsPerPage = event.rows;
            this.page = event.first / event.rows;
        }
        this.loadComments();
    }

    submitComment(parentUuid: string, level: number) {
        const comment = new Comment();
        comment.uuid = uuid().replace(/-/g, '').toLowerCase();
        comment.solutionUuid = this.solution.uuid;
        comment.parentUuid = parentUuid;
        comment.commentText = this.commentText;
        comment.level = level;

        this.ummClient.create_comment({
            comment,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.loadComments();
                    this.commentText = '';
                    this.ummClient.update_solution_comment_count({
                        solutionId: this.solution.id,
                    }).subscribe();
                    this.solution.commentCount++;
                }
            }
        );
    }

    submitReplyComment(parentComment: Comment) {
        const comment = new Comment();
        comment.uuid = uuid().replace(/-/g, '').toLowerCase();
        comment.solutionUuid = this.solution.uuid;
        comment.parentUuid = parentComment.uuid;
        comment.commentText = parentComment.replyText;
        comment.level = parentComment.level + 1;

        this.ummClient.create_comment({
            comment,
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    this.loadReplyComments(parentComment);
                    parentComment.viewReply = true;
                    parentComment.replyText = '';
                    this.toggleReplyComment(parentComment);
                    this.ummClient.update_solution_comment_count({
                        solutionId: this.solution.id,
                    }).subscribe();
                    this.solution.commentCount++;
                }
            }
        );
    }

    toggleReplyComment(comment: Comment) {
        comment.isReplying = !comment.isReplying;
    }

    toggleViewComment(comment: Comment) {
        comment.viewReply = !comment.viewReply;
    }

    loadReplyComments(comment: Comment) {
        this.ummClient.get_comments({
            solutionUuid: this.solution.uuid,
            parentUuid: comment.uuid,
            sort: ['id,desc'],
        }).subscribe(
            (res) => {
                if (res.body['status'] === 'ok') {
                    comment.replyComments = res.body['value']['results'];
                    if (comment.level < 2) {
                        comment.replyComments.forEach(
                            (replyComment) => {
                                this.loadReplyComments(replyComment);
                            }
                        );
                    }
                }
            }
        );
    }

    canDeleteComment(comment: Comment) {
        return (!comment.replyComments || (comment.replyComments.length < 1)
                    && (this.isManager || (this.userLogin === comment.userLogin)));
    }

    deleteComment(comment: Comment) {
        this.confirmationService.confirm({
            target: event.target,
            message: '确定要删帖？',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: '是',
            rejectLabel: '否',
            accept: () => {
                this.ummClient.delete_comment({
                    commentId: comment.id,
                }).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.loadComments();
                            this.ummClient.update_solution_comment_count({
                                solutionId: this.solution.id,
                            }).subscribe();
                            this.solution.commentCount--;
                        }
                    }
                );
            },
            reject: () => {}
        });
    }

    deleteReply(reply: Comment, parent: Comment) {
        this.confirmationService.confirm({
            target: event.target,
            message: '确定要删帖？',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: '是',
            rejectLabel: '否',
            accept: () => {
                this.ummClient.delete_comment({
                    commentId: reply.id,
                }).subscribe(
                    (res) => {
                        if (res.body['status'] === 'ok') {
                            this.loadReplyComments(parent);
                            this.ummClient.update_solution_comment_count({
                                solutionId: this.solution.id,
                            }).subscribe();
                            this.solution.commentCount--;
                        }
                    }
                );
            },
            reject: () => {}
        });
    }

    deploySolution() {
        this.router.navigate(['/deploy/deploy/' + this.solution.uuid]);
    }

    gotoAbility() {
        window.location.href = '/popen/#/ability/' + this.solutionUuid;
    }

    gotoPersonal(authorLogin: string) {
        this.router.navigate(['/personal/' + authorLogin]);
    }

    gotoStargazers() {
        if (this.solution.starCount > 0) {
            this.router.navigate(['/stargazer/' + this.solution.uuid + '/' + this.solution.name]);
        }
    }

}
