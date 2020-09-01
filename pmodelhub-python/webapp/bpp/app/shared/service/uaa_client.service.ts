import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class UaaClient {

    url = SERVER_API_URL + 'uaa/api/data';

    constructor(
        private http: HttpClient,
    ) {}

    create_authority(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'create_authority',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_authorities(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_authorities',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    delete_authority(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'delete_authority',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_current_account(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_current_account',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    update_current_account(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'update_current_account',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    change_password(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'change_password',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    register_user(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'register_user',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    activate_user(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'activate_user',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    password_reset_init(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'password_reset_init',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    password_reset_finish(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'password_reset_finish',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    create_user(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'create_user',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_users(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_users',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    find_user(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'find_user',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    update_user(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'update_user',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    delete_user(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'delete_user',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_login_exist(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_login_exist',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_email_exist(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_email_exist',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_phone_exist(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_phone_exist',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_verify_code(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_verify_code',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_random_picture(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_random_picture',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    create_application(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'create_application',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_applications(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_applications',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    find_application(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'find_application',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    update_application(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'update_application',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    delete_application(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'delete-application',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    create_article(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'create_article',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_articles(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_articles',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    find_article(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'find_article',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    update_article(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'update_article',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    delete_article(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'delete_article',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_attachments(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_attachments',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    delete_attachment(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'delete_attachment',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    send_message(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'send_message',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    send_multicast_message(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'send_multicast_message',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_messages(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_messages',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    mark_message_viewed(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'mark_message_viewed',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    mark_message_deleted(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'password_reset_init',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    delete_message(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'delete_message',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_unread_message_count(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_unread_message_count',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

}
