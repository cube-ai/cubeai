import { Injector } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/do';
import { Router } from '@angular/router';
import {Principal} from '../../shared';
import { MatDialog } from '@angular/material';

export class AuthExpiredInterceptor implements HttpInterceptor {

    constructor(
        private injector: Injector,
        public dialog: MatDialog,
    ) {}

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).do((event: HttpEvent<any>) => {}, (err: any) => {
            if (err instanceof HttpErrorResponse) {
                if (err.status === 401) {

                    const principal = this.injector.get(Principal);
                    const router = this.injector.get(Router);

                    if (principal.isAuthenticated()) {
                        // 因登录超时等原因，访问网络时返回401未授权访问，但是本地principal还存在。
                        // 则清空本地principal，然后提示重新登录，登录后重定向至原先页面。
                        principal.authenticate(null);

                        // TODO: 经测试，这里并没有弹出登录对话框，原因未知。
                        const reason = '访问被拒绝，请重新登录...';
                        const redirectUrl = (window.location.pathname + '@' + router.url).replace(/\//g, '$');
                        window.location.href = '/#/login/' + redirectUrl + '/' + reason;
                    } else {
                        // 否则浏览器本地已经是未登录状态，则简单重定向至首页
                        if (router.url !== '/') {
                            router.navigate(['/']);
                        }
                    }
                }
            }
        });
    }
}
