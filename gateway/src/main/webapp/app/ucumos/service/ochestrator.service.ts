import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {createRequestOption} from '../../shared';
import {Solution} from '../model/solution.model';
import { SERVER_API_URL } from '../../app.constants';

@Injectable()
export class OchestratorService {
    private resourceUrl = SERVER_API_URL + 'umo/api/solutions';

    constructor(private http: HttpClient) { }
    build_url(verb, params): string {
        return this.resourceUrl + verb + '?' + Object.keys(params).map(function(k) {
            return k + '=' + encodeURIComponent(params[k]);
        }).join('&');
    }

    createNodes(body: any): Observable<HttpResponse<any>> {
        return this.http.post<any>(this.resourceUrl + '/nodes', body, { observe: 'response' });
    }
    updateNode(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + '/nodes', body, { observe: 'response' });
    }
    deleteNode(solutionId, userId, nodeId): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${solutionId}/nodes/${nodeId}/${userId}`, {observe: 'response' });
    }

    createLink(body: any): Observable<HttpResponse<any>> {
        return this.http.post<any>(this.resourceUrl + '/links', body, { observe: 'response' });
    }
    updateLink(body: any): Observable<HttpResponse<any>> {
        return this.http.put<any>(this.resourceUrl + '/links', body, { observe: 'response' });
    }
    deleteLink(solutionId, userId, linkId): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${solutionId}/links/${linkId}/${userId}`, {observe: 'response' });
    }
    // userId: get_userId(),
    // visibilityLevel : tempArr[key]
    getCompositeSolutionGraphs(body: any): Observable<HttpResponse<any>> {
        const options = createRequestOption(body);
        // return this.http.get<any>(this.build_url('/compositeSolutionGraphs', body), { observe: 'response' });
        return this.http.get<any>(this.resourceUrl + '/compositeSolutionGraphs', { params: options, observe: 'response' });
    }

    getTgif(body: any): Observable<HttpResponse<any>> {
        const options = createRequestOption(body);
        return this.http.get<any>(this.resourceUrl + '/tosca', { params: options, observe: 'response' });
    }

    getProfobuf(body: any): Observable<HttpResponse<any>> {
        const options = createRequestOption(body);
        return this.http.get<any>(this.resourceUrl + '/protobuf', { params: options, observe: 'response' });
    }

    getMatchingModels(body: any): Observable<HttpResponse<any>> {
        const options = createRequestOption(body);
        return this.http.get<any>(this.resourceUrl + '/matchingModels', { params: options, observe: 'response' });
    }
}
