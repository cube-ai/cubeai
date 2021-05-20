import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class UmmClient {

    url = SERVER_API_URL + 'umm/api/data';

    constructor(
        private http: HttpClient,
    ) {}

    get_solutions(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_solutions',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    update_solution_baseinfo(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'update_solution_baseinfo',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    update_solution_admininfo(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'update_solution_admininfo',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    update_solution_active(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'update_solution_active',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    update_solution_picture_url(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'update_solution_picture_url',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    update_solution_star_count(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'update_solution_star_count',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    update_solution_view_count(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'update_solution_view_count',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    update_solution_download_count(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'update_solution_download_count',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    update_solution_comment_count(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'update_solution_comment_count',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    delete_solution(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'delete_solution',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_artifacts(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_artifacts',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_documents(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_documents',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    find_description(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'find_description',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    update_description(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'update_description',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_tasks(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_tasks',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_task_steps(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_task_steps',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    create_star(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'create_star',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_stars(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_stars',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    delete_task(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'delete_task',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_user_stared_uuid_list(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_user_stared_uuid_list',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    delete_star_by_target_uuid(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'delete_star_by_target_uuid',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    delete_star(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'delete_star',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_my_credit(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_my_credit',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_credits(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_credits',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    update_credit(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'update_credit',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_credit_history(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_credit_history',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    create_comment(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'create_comment',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    get_comments(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'get_comments',
            args,
        };
        return this.http.post<any>(this.url, body, { observe: 'response' });
    }

    delete_comment(args): Observable<HttpResponse<any>> {
        const body = {
            action: 'delete_comment',
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

}
