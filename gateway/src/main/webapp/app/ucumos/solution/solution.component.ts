import {Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog, MatPaginator, PageEvent} from '@angular/material';
import { Location } from '@angular/common';
import { Subscription } from 'rxjs/Subscription';
import {ConfirmService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS, SnackBarService, GlobalService} from '../../shared';
import {Principal, User} from '../../account';
import {Solution} from '../model/solution.model';
import {Artifact} from '../model/artifact.model';
import {Document} from '../model/document.model';
import {
    AbilityService,
    ArtifactService, CommentService, DescriptionService, DocumentService, DownloadService, PublishRequestService,
    SolutionFavoriteService, SolutionRatingService,
    SolutionService
} from '../';
import { saveAs } from 'file-saver';
import {FileUploader} from 'ng2-file-upload';
import {SERVER_API_URL} from '../../app.constants';
import {CookieService} from 'ngx-cookie';
import {FileItem} from 'ng2-file-upload/file-upload/file-item.class';
import {PictureSelectComponent} from './picture-select.component';
import {ApproveHistoryComponent} from './approve-history.component';
import {ShareSolutionComponent} from './share-solution.component';
import {SolutionFavorite} from '../model/solution-favorite.model';
import {Description} from '../model/description.model';
import {SolutionRating} from '../model/solution-rating.model';
import {Comment} from '../model/comment.model';
import {v4 as uuid} from 'uuid';

@Component({
    templateUrl: './solution.component.html',
    styleUrls: [
        './solution.css',
        '../ucumos-datapage.css',
    ],
})
export class SolutionComponent implements OnInit, OnDestroy {

    currentUser: User;
    subscription: Subscription;
    solutionUuid: string;
    publishRequestId: number;
    isEditing = false;
    isReviewingPublish = false;
    isReviewingUnpublish = false;
    isViewing = false;
    isOwner = false;
    isReviewer = false;
    canPublish = false;
    canUnpublish = false;
    requestReason: string;
    reviewComment: string;
    openAbilityUrl: string;

    solution: Solution = null;
    description: Description = new Description();
    editingDescription = false;
    sendingAction = false;
    metadataText: string;
    protobufText: string;
    artifacts: Artifact[];
    documents: Document[];
    uploader: FileUploader;
    solutionFavorite: SolutionFavorite;
    solutionRating: SolutionRating;
    comments: Comment[] = [];
    commentText: string;

    // 用于Comment的分页显示
    @ViewChild(MatPaginator) paginator: MatPaginator;
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = ITEMS_PER_PAGE;
    previousItemsPerPage = ITEMS_PER_PAGE;
    totalItems: number;
    page = 1;
    previousPage = 1;

    constructor(
        private globalService: GlobalService,
        private dialog: MatDialog,
        private route: ActivatedRoute,
        private router: Router,
        private principal: Principal,
        private location: Location,
        private solutionService: SolutionService,
        private descriptionService: DescriptionService,
        private snackBarService: SnackBarService,
        private confirmService: ConfirmService,
        private downloadService: DownloadService,
        private artifactService: ArtifactService,
        private documentService: DocumentService,
        private cookieService: CookieService,
        private publishRequestService: PublishRequestService,
        private solutionFavoriteService: SolutionFavoriteService,
        private solutionRatingService: SolutionRatingService,
        private commentService: CommentService,
        private abilityService: AbilityService,
    ) {
    }

    goBack() {
        this.location.back();
    }

    ngOnDestroy() {
        if (this.isEditing) {
            this.solutionService.updateBaseinfo({
                id: this.solution.id,
                company: this.solution.company,
                coAuthors: this.solution.coAuthors,
                version: this.solution.version,
                summary: this.solution.summary,
                tag1: this.solution.tag1,
                tag2: this.solution.tag2,
                tag3: this.solution.tag3,
                modelType: this.solution.modelType,
                toolkitType: this.solution.toolkitType,
            }).subscribe(() => {
                if (this.isReviewer) {
                    this.solutionService.updateSubjects({
                        id: this.solution.id,
                        subject1: this.solution.subject1,
                        subject2: this.solution.subject2,
                        subject3: this.solution.subject3,
                        displayOrder: this.solution.displayOrder,
                    }).subscribe();
                }
            });

            if (this.editingDescription) {
                this.descriptionService.updateContent({
                    id: this.description.id,
                    content: this.description.content,
                }).subscribe();
            }
        } else if (this.isReviewer) {
            this.solutionService.updateSubjects({
                id: this.solution.id,
                subject1: this.solution.subject1,
                subject2: this.solution.subject2,
                subject3: this.solution.subject3,
                displayOrder: this.solution.displayOrder,
            }).subscribe();
        }

        this.subscription.unsubscribe();
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }

        this.currentUser = this.principal.getCurrentAccount() ? this.principal.getCurrentAccount() : null;
        this.subscription = this.route.params.subscribe((params) => {
            this.solutionUuid = params['solutionUuid'];
            this.isEditing = params['openMode'] === 'edit';
            this.isReviewingPublish = params['openMode'] === 'reviewpublish';
            this.isReviewingUnpublish = params['openMode'] === 'reviewunpublish';
            this.isViewing = params['openMode'] === 'view';
            this.publishRequestId =  params['publishRequestId'];
        });

        this.solutionService.query({
                uuid: this.solutionUuid,
            }).subscribe(
            (res) => {
                this.solution = res.body[0];

                if (this.isViewing) {
                    this.solutionService.updateViewCount({
                        id: this.solution.id,
                    }).subscribe();
                    this.solution.viewCount ++;
                }

                this.isOwner = this.currentUser.login === this.solution.authorLogin;
                this.isReviewer = this.currentUser.authorities.includes('ROLE_MANAGER');

                if (this.isEditing  && !this.isOwner) {
                    this.snackBarService.error('非正常访问！');
                    this.goBack();
                }
                if ((this.isReviewingPublish || this.isReviewingUnpublish)  && !this.isReviewer) {
                    this.snackBarService.error('非正常访问！');
                    this.goBack();
                }
                if (this.isViewing  && !this.isOwner && this.solution.publishStatus === '下架') {
                    this.snackBarService.error('模型已下架，无法访问！');
                    this.goBack();
                }
                if (this.isViewing  && !this.isOwner && !this.solution.active) {
                    this.snackBarService.error('模型已被删除，无法访问！');
                    this.goBack();
                }

                if (this.isViewing) {
                    this.abilityService.query({
                        isPublic: true,
                        solutionUuid: this.solutionUuid,
                        status: '运行',
                    }).subscribe(
                        (res1) => {
                            if (res1 && res1.body.length > 0) {
                                this.openAbilityUrl = '/ai-ability/ability/' + res1.body[0].uuid + '/view';
                            }
                        }
                    );
                }

                this.updateRenderParameters();

                this.loadDescription();
                this.loadMetadataText();
                this.loadProtobufText();
                this.loadArtifactData();
                this.loadDocumentData();
                this.loadSolutionFavorite();
                this.loadSolutionRating();
                this.loadComments();

                this.uploader = new FileUploader({
                    url: SERVER_API_URL + 'zuul/umu/api/documents/' + this.solution.uuid,
                    method: 'POST',
                    itemAlias: 'document',
                });
                this.uploader.onBeforeUploadItem = (fileItem) => {
                    fileItem.headers.push({name: 'X-XSRF-TOKEN', value: this.cookieService.get('XSRF-TOKEN')});
                    return fileItem;
                };
            }, () => {
                this.snackBarService.error('获取模型数据失败！');
                this.goBack();
            }
        );
    }

    updateRenderParameters() {
        this.canPublish = this.isEditing && this.solution.active && (this.solution.publishStatus === '下架') && (this.solution.publishRequest === '无申请');
        this.canUnpublish = this.isEditing && this.solution.active && (this.solution.publishStatus === '上架') && (this.solution.publishRequest === '无申请');
    }

    upload(fileItem: FileItem) {
        fileItem.onSuccess = () => {
            this.loadDocumentData();
        };

        fileItem.onError = () => {
            this.loadDocumentData();
        };

        if (fileItem.file.size > 20 * 1024 * 1024) {
            this.snackBarService.error('上传文档不能大于20MB！');
            fileItem.remove();
            return;
        }

        if (this.documents.length > 4) {
            this.snackBarService.error('一个模型最多5个文档！请删除现有文档后再上传新文档...');
            return;
        }

        if (this.findExistDocument(fileItem.file.name)) {
            this.snackBarService.error('本模型中已存在同名文档！请删除现有文档后再上传新文档...');
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
        this.descriptionService.query({
            solutionUuid: this.solution.uuid,
        }).subscribe(
            (res) => {
                if (res.body && res.body.length > 0) {
                    this.description = res.body[0];
                }
            }
        );
    }

    loadMetadataText() {
        this.downloadService.getMetadataText(this.solution.uuid).subscribe(
            (res) => {
                this.metadataText = '\n' + JSON.stringify(JSON.parse(res.body['metadata']), null, 4);
            }
        );
    }

    loadProtobufText() {
        this.downloadService.getProtobufText(this.solution.uuid).subscribe(
            (res) => {
                this.protobufText = res.body['protobuf'];
            }
        );
    }

    loadArtifactData() {
        this.artifactService.query({
            solutionUuid: this.solution.uuid,
        }).subscribe(
            (res) => {
                this.artifacts = res.body;
            }
        );
    }

    loadDocumentData() {
        this.documentService.query({
            solutionUuid: this.solution.uuid,
        }).subscribe(
            (res) => {
                this.documents = res.body;
            }
        );
    }

    loadSolutionFavorite() {
        this.solutionFavoriteService.query({
            userLogin: this.currentUser.login,
            solutionUuid: this.solutionUuid,
        }).subscribe(
            (res) => {
                if (res.body && res.body.length > 0) {
                    this.solutionFavorite = res.body[0];
                } else {
                    this.solutionFavorite = null;
                }
            }
        );
    }

    loadSolutionRating() {
        this.solutionRatingService.query({
            userLogin: this.currentUser.login,
            solutionUuid: this.solutionUuid,
        }).subscribe(
            (res) => {
                if (res.body && res.body.length > 0) {
                    this.solutionRating = res.body[0];
                } else {
                    this.solutionRating = new SolutionRating();
                    this.solutionRating.solutionUuid = this.solution.uuid;
                    this.solutionRatingService.create(this.solutionRating).subscribe(
                        (res1) => {
                            this.solutionRating = res1.body;
                        }
                    );
                }
            }
        );
    }

    isTextFile(url: string): boolean {
       const ext = url.substring(url.lastIndexOf('.') + 1);
       return ext === 'json' || ext === 'proto' || ext === 'txt';
    }

    // 目前不用这种方式下载
    downloadArtifactFromUmu(url: string, fileName) {
        this.downloadService.downloadFile(url).subscribe(
            (res) => {
                const blob = new Blob([res.body], {type: 'application/octet-stream'});
                saveAs(blob, fileName);
            }
        );
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
        this.solutionService.updateDownloadCount({
            id: this.solution.id,
        }).subscribe(
            (res) => {
                this.solution = res.body;
            }
        );
    }

    deleteDocument(document: Document) {
        this.confirmService.ask('确定要删除该文档？').then((confirm) => {
            if (confirm) {
                this.documentService.deleteDocument(document.id).subscribe(
                    () => {
                        this.loadDocumentData();
                    }
                );
            }
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
        this.snackBarService.info('拉取docker镜像命令已复制到剪贴板...');
        this.solutionService.updateDownloadCount({
            id: this.solution.id,
        }).subscribe(
            (res) => {
                this.solution = res.body;
            }
        );
    }

    enterEditDescription() {
        this.editingDescription = true;
    }

    saveDescription() {
        this.descriptionService.updateContent({
            id: this.description.id,
            content: this.description.content,
        }).subscribe(
            (res) => {
                this.description = res.body;
                this.editingDescription = false;
            }
        );
    }

    deleteSolution() {
        this.confirmService.ask('确定要删除该模型？').then((confirm) => {
            if (confirm) {
                this.solutionService.updateActive({
                    id: this.solution.id,
                    active: false,
                }).subscribe(
                    (res) => {
                        this.solution = res.body;
                        this.isEditing = false;
                        this.isViewing = true;
                        this.updateRenderParameters();
                        this.snackBarService.success('模型已被删除！你仍可以从[回收站]中找回该模型...');
                    }
                );
            }
        });
    }

    restoreSolution() {
        this.confirmService.ask('确定要找回该模型？').then((confirm) => {
            if (confirm) {
                this.solutionService.updateActive({
                    id: this.solution.id,
                    active: true,
                }).subscribe(
                    (res) => {
                        this.solution = res.body;
                        this.isEditing = true;
                        this.isViewing = false;
                        this.updateRenderParameters();
                        this.snackBarService.success('模型已从回收站中找回！');
                    }
                );
            }
        });
    }

    favoriteSolution() {
        if (this.solutionFavorite) {
            this.solutionFavoriteService.delete(this.solutionFavorite.id).subscribe(
                () => {
                    this.solutionFavorite = null;
                }
            );
        } else {
            const solutionFavorite = new SolutionFavorite();
            solutionFavorite.solutionUuid = this.solutionUuid;
            solutionFavorite.solutionName = this.solution.name;
            solutionFavorite.solutionAuthor = this.solution.authorLogin;
            solutionFavorite.solutionCreatedDate = this.solution.createdDate;
            this.solutionFavoriteService.create(solutionFavorite).subscribe(
                () => {
                    this.loadSolutionFavorite();
                }
            );
        }
    }

    shareSolution() {
        const dialogRef = this.dialog.open(ShareSolutionComponent, {
            width: '450px',
            data: {
                fromUserLogin: this.currentUser.login,
                solution: this.solution,
            },
        });
    }

    changeSolutionPicture() {
        const config = {
            width: '800px',
            data: {
                pictureUrl: this.solution.pictureUrl,
            },
        };
        if (window.screen.height < 800) {
            config['height'] = '600px';
        }
        const dialogRef = this.dialog.open(PictureSelectComponent, config);

        dialogRef.afterClosed().subscribe((selectedImgFile) => {
            if (selectedImgFile) {
                const fileUploader = new FileUploader({
                    url: SERVER_API_URL + 'zuul/umu/api/solution-picture/' + this.solution.uuid,
                    method: 'POST',
                    itemAlias: 'picture',
                });
                fileUploader.onBeforeUploadItem = (fileItem) => {
                    fileItem.headers.push({name: 'X-XSRF-TOKEN', value: this.cookieService.get('XSRF-TOKEN')});
                    return fileItem;
                };
                fileUploader.addToQueue([selectedImgFile]);
                fileUploader.queue[0].onSuccess = () => {
                    // 上传成功后, 头像url已保存在数据库中，因此需要从数据库中拉去数据
                    this.solutionService.getPictureUrl(this.solution.id).subscribe(
                        (res) => {
                            this.solution.pictureUrl = res.body['pictureUrl'];
                            this.updateRenderParameters();
                            this.snackBarService.success('模型头像更新成功！');
                        }, () => {
                            this.snackBarService.error('模型头像更新失败！');
                        }
                    );
                };

                fileUploader.queue[0].upload();
            }
        });
    }

    viewApproveHistory() {
        const config = {
            width: '900px',
            data: {
                solutionUuid: this.solution.uuid,
                solutionName: this.solution.name,
            },
        };
        if (window.screen.height < 800) {
            config['height'] = '600px';
        }
        const dialogRef = this.dialog.open(ApproveHistoryComponent, config);
    }

    requestPublish() {
        // 申请上架之前先保存solution的基础信息
        this.solutionService.updateBaseinfo({
            id: this.solution.id,
            company: this.solution.company,
            coAuthors: this.solution.coAuthors,
            version: this.solution.version,
            summary: this.solution.summary,
            tag1: this.solution.tag1,
            tag2: this.solution.tag2,
            tag3: this.solution.tag3,
            modelType: this.solution.modelType,
            toolkitType: this.solution.toolkitType,
        }).subscribe();

        this.confirmService.ask('确定要申请上架？').then((confirm) => {
            if (confirm) {
                this.sendingAction = true;
                this.solutionService.requestPublish({
                    id: this.solution.id,
                    requestType: '申请上架',
                    requestReason: this.requestReason,
                }).subscribe(
                    (res) => {
                        this.solution.publishRequest = res.body.publishRequest;
                        this.isEditing = false;
                        this.isViewing = true;
                        this.updateRenderParameters();
                        this.snackBarService.success('已发送模型上架申请！正在等待审批...');
                        this.sendingAction = false;
                    }, () => {
                        this.snackBarService.error('发送申请上架请求失败！');
                        this.sendingAction = false;
                    }
                );
            }
        });
    }

    requestUnpublish() {
        this.confirmService.ask('确定要申请下架？').then((confirm) => {
            if (confirm) {
                this.sendingAction = true;
                this.solutionService.requestPublish({
                    id: this.solution.id,
                    requestType: '申请下架',
                    requestReason: this.requestReason,
                }).subscribe(
                    (res) => {
                        this.solution.publishRequest = res.body.publishRequest;
                        this.isEditing = false;
                        this.isViewing = true;
                        this.updateRenderParameters();
                        this.snackBarService.success('已发送模型下架申请！正在等待审批...');
                        this.sendingAction = false;
                    }, () => {
                        this.snackBarService.error('发送申请下架请求失败！');
                        this.sendingAction = false;
                    }
                );
            }
        });
    }

    approvePublish(approve: boolean) {
        this.sendingAction = true;
        this.solutionService.approvePublish({
            solutionId: this.solution.id,
            toPublish: this.isReviewingPublish,
            approved: approve,
            reviewComment: this.reviewComment,
            publishRequestId: this.publishRequestId,
        }).subscribe(
            (res) => {
                this.solution = res.body;
                this.isReviewingPublish = false;
                this.isReviewingUnpublish = false;
                this.isViewing = true;
                this.updateRenderParameters();
                this.snackBarService.success('审批完成！');
                this.sendingAction = false;
            }, () => {
                this.snackBarService.error('发送审批结果失败!');
                this.sendingAction = false;
            }
        );
    }

    ratingSolution(score: number) {
        if (!this.isViewing) {
            return;
        }
        this.solutionRatingService.updateScore({
            id: this.solutionRating.id,
            score,
        }).subscribe(
            (res) => {
                this.solutionRating = res.body;
                this.solutionService.updateRatingStats({
                    id: this.solution.id,
                }).subscribe(
                    (res1) => {
                        this.solution = res1.body;
                        // this.snackBarService.success('点评成功！');
                    }
                );
            }
        );
    }

    loadComments() {
        this.commentService.query({
            solutionUuid: this.solution.uuid,
            parentUuid: '0',
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: ['id,desc'],
        }).subscribe(
            (res) => {
                this.totalItems = parseInt(res.headers.get('X-Total-Count'), 10);
                this.comments = res.body;
                this.comments.forEach(
                    (comment) => {
                        this.loadReplyComments(comment);
                    }
                );
            }
        );
    }

    reloadPage(pageEvent: PageEvent) {
        this.itemsPerPage = pageEvent.pageSize;
        this.page = pageEvent.pageIndex + 1;

        if (this.previousPage !== this.page) {
            this.previousPage = this.page;
            this.loadComments();
        }

        if (this.itemsPerPage !== this.previousItemsPerPage) {
            this.previousItemsPerPage = this.itemsPerPage;
            this.loadComments();
        }
    }

    submitComment(parentUuid: string, level: number) {
        const comment = new Comment();
        comment.uuid = uuid().replace(/-/g, '').toLowerCase();
        comment.solutionUuid = this.solution.uuid;
        comment.parentUuid = parentUuid;
        comment.commentText = this.commentText;
        comment.level = level;
        this.commentService.create(comment).subscribe(
            () => {
                this.loadComments();
                this.commentText = '';
                this.solutionService.updateCommentCount({
                    id: this.solution.id,
                }).subscribe(
                    (res) => {
                        this.solution = res.body;
                    }
                );
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
        this.commentService.create(comment).subscribe(
            () => {
                this.loadReplyComments(parentComment);
                parentComment.viewReply = true;
                parentComment.replyText = '';
                this.toggleReplyComment(parentComment);
                this.solutionService.updateCommentCount({
                    id: this.solution.id,
                }).subscribe(
                    (res) => {
                        this.solution = res.body;
                    }
                );
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
        this.commentService.query({
            solutionUuid: this.solution.uuid,
            parentUuid: comment.uuid,
            sort: ['id,desc'],
        }).subscribe(
            (res) => {
                comment.replyComments = res.body;
                if (comment.level < 2) {
                    comment.replyComments.forEach(
                        (replyComment) => {
                            this.loadReplyComments(replyComment);
                        }
                    );
                }
            }
        );
    }

    canDeleteComment(comment: Comment) {
        return this.isViewing
            && (!comment.replyComments
                || (comment.replyComments.length < 1
                    && (this.currentUser.authorities.includes('ROLE_ADMIN') || this.currentUser.login === comment.userLogin)));
    }

    deleteComment(comment: Comment) {
        this.confirmService.ask('确定要删帖？').then((confirm) => {
            if (confirm) {
                this.commentService.delete(comment.id).subscribe(
                    () => {
                        this.loadComments();
                    }
                );
            }
        });
    }

    deleteReply(reply: Comment, parent: Comment) {
        this.confirmService.ask('确定要删帖？').then((confirm) => {
            if (confirm) {
                this.commentService.delete(reply.id).subscribe(
                    () => {
                        this.loadReplyComments(parent);
                    }
                );
            }
        });
    }

    deploySolution() {
        this.router.navigate(['/ucumos/deploy/deploy/' + this.solution.uuid]);
    }

    jumpToAbility() {
        this.router.navigate([this.openAbilityUrl]);
    }

}
