import {Component, OnInit, ViewChild} from '@angular/core';
import { MatDialog } from '@angular/material';
import {LoginComponent, RegisterComponent} from '../../account';
import {Router} from '@angular/router';
import {Principal} from '../../account';
import {GlobalService, ITEMS_PER_PAGE, PAGE_SIZE_OPTIONS} from '../../shared';
import {MatPaginator, PageEvent} from '@angular/material';
import {Solution} from '../../ucumos/model/solution.model';
import {SolutionService} from '../../ucumos';
import {ArticleService} from '../../admin';

@Component({
    templateUrl: './home.component.html',
    styleUrls: [
        'home.css',
        '../../ucumos/ucumos-datapage.css'
    ]
})
export class HomeComponent implements OnInit {
    homepageIntro = '';
    homepageNews = '';
    homepageArch = '';
    homepagePartners = '';

    solutions: Solution[] = [];
    @ViewChild(MatPaginator) paginator: MatPaginator;
    pageSizeOptions = PAGE_SIZE_OPTIONS;
    itemsPerPage = 10;
    previousItemsPerPage = 10;
    totalItems: number;
    page = 1;
    previousPage = 1;
    predicate = 'displayOrder';
    reverse = false;

    constructor(private router: Router,
                private principal: Principal,
                public dialog: MatDialog,
                private solutionService: SolutionService,
                private articleService: ArticleService,
                private globalService: GlobalService,
                ) {
    }

    ngOnInit() {
        this.updateCurrentAccountAndNavigate();
    }

    updateCurrentAccountAndNavigate() {
        this.principal.updateCurrentAccount().then((currentAccount) => {
            if (currentAccount) {
                this.globalService.getHeader().getNewMsgCount();
                this.navigate();
            } else {
                this.loadAll();
            }

        });
    }

    navigate() {
        if (this.principal.hasAuthority('ROLE_USER')
            || this.principal.hasAuthority('ROLE_MANAGER')
            || this.principal.hasAuthority('ROLE_DEVELOPER')) {
            this.router.navigate(['/ucumos/market']);
        } else if (this.principal.hasAuthority('ROLE_ADMIN')) {
            this.router.navigate(['/admin/user-management']);
        } else if (this.principal.hasAuthority('ROLE_CONTENT')) {
            this.router.navigate(['/admin/bulletin']);
        } else {
            this.router.navigate(['/ucumos/market']);
        }
    }

    loadAll() {
        this.loadHomePageArticles();
        this.loadSolutions();
    }

    loadHomePageArticles() {
        this.articleService.query({
            subject1: 'homepage-intro',
        }).subscribe((res) => {
            if (res.body && res.body.length > 0) {
                this.homepageIntro = res.body[0].content;
            }
        });

        this.articleService.query({
            subject1: 'homepage-news',
        }).subscribe((res) => {
            if (res.body && res.body.length > 0) {
                this.homepageNews = res.body[0].content;
            }
        });

        this.articleService.query({
            subject1: 'homepage-arch',
        }).subscribe((res) => {
            if (res.body && res.body.length > 0) {
                this.homepageArch = res.body[0].content;
            }
        });

        this.articleService.query({
            subject1: 'homepage-partners',
        }).subscribe((res) => {
            if (res.body && res.body.length > 0) {
                this.homepagePartners = res.body[0].content;
            }
        });
    }

    loadSolutions() {
        const queryOptions = {
            active: true,
            publishStatus: '上架',
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort(),
        };

        this.solutionService.query(queryOptions).subscribe(
            (res) => this.onSuccess(res.body, res.headers),
            (res) => this.onError(res)
        );
    }

    trackIdentity(index, item: Solution) {
        return item.id;
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id' + ',' + (this.reverse ? 'asc' : 'desc'));
        }
        return result;
    }

    reloadPage(pageEvent: PageEvent) {
        this.itemsPerPage = pageEvent.pageSize;
        this.page = pageEvent.pageIndex + 1;

        if (this.previousPage !== this.page) {
            this.previousPage = this.page;
            this.transition();
        }

        if (this.itemsPerPage !== this.previousItemsPerPage) {
            this.previousItemsPerPage = this.itemsPerPage;
            this.transition();
        }
    }

    transition() {
        this.refresh();
    }

    refresh() {
        this.loadAll();
    }

    private onSuccess(data, headers) {
        this.totalItems = headers.get('X-Total-Count');
        this.solutions = data;
    }

    private onError(error) {
    }

    viewSolution(solution) {
        const config = {
            width: '600px',
            data: {
                reason: '登录后才能查看模块详情，请登录......',
                redirectUrl: '/ucumos/solution/' + solution.uuid + '/' + 'view',
            }
        };
        if (window.screen.height < 800) {
            config['height'] = '600px';
        }
        this.dialog.open(LoginComponent, config);
    }

    login() {
        const config = {
            width: '600px',
            data: {},
        };
        if (window.screen.height < 800) {
            config['height'] = '600px';
        }
        this.dialog.open(LoginComponent, config);
    }

    registerUser() {
        const config = {
            width: '800px',
            data: {
                activate: false,
            },
        };
        if (window.screen.height < 800) {
            config['height'] = '600px';
        }
        this.dialog.open(RegisterComponent, config);
    }

    getRatingWidth(rating: number) {
        if (rating !== null) {
            const starWidthPx = Math.round((rating / 5) * 60);
            return starWidthPx + 'px';
        } else {
            return 0;
        }
    }

}
