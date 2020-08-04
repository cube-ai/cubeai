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

                    // 因未登录或登录超时等原因，访问网络时返回401未授权访问时，但是本地principal还存在，此时应清空本地principal
                    // 否则浏览器本地已经是未登录状态，则什么都不做，停留在当前页面
                    // (其实没必要在这里进行处理，因为大多数页面在访问时会首先更新用户身份信息，主动清空本地缓存)
                    if (principal.isAuthenticated()) {
                        principal.authenticate(null);
                    }
                }
            }
        });
    }
}
