import {Component, OnInit} from '@angular/core';
import {ConfirmService, SnackBarService} from '../../shared';
import {Principal, User} from '../../account';
import {SolutionService} from '../service/solution.service';
import {FormControl} from '@angular/forms';

import {select, selectAll, event} from 'd3-selection';
import {drag} from 'd3-drag';
import { toInt} from 'ngx-bootstrap/chronos/utils/type-checks';
import { Observable} from 'rxjs/Rx';
import { map, startWith} from 'rxjs/operators';
import { MatDialog} from '@angular/material';
import { AddSolutionComponent} from './ochestrator.addSolution.component';
import {MyPath, Relations} from '../model/link.model';
import {MyNode, Ndata, Node} from '../model/node.model';
import {MyCircle, MyPort} from '../model/circle.model';
import {OchestratorService} from '../service/ochestrator.service';
import {CompositeSolutionService} from '../service/compositeSolution.service';
import {Solution} from '../model/solution.model';
import {HttpResponse} from '@angular/common/http';
import {OchestratorEditeLinkComponent} from './ochestrator.editeLink.component';
import {Router} from '@angular/router';
import {DocumentService, DownloadService} from '../../ucumos';
import {UpdateSolutionComponent} from './ochestrator.updateSolution.component';

class MySolution {
    uuid: any;
    name: any;
    version: any;
    description: any;
    workflow: any;
    activeLine = null;
    activeLineGroup = null;
    points = [];
    translate = null;
    drawLine = false;
    dx = 0;
    dy = 0;
    dragElem = null;
    nodes: any;
    // gNodes: any;
    paths: MyPath[];
    curPath: MyPath;
}
@Component({
    templateUrl: './ochestrator.component.html',
    styleUrls: [
        '../ucumos-datapage.css',
        'ochestrator.css',
    ],
})
export class OchestratorComponent implements OnInit {

    newCompositionButton = false;
    clearCompositeSolutionButton = false;
    closeCompositeSolutionButton = false;
    pathClicked = false;
    pathClickedObject: MyPath;
    currentUser: User;
    userLogin: string;

    categoryNames: any[];
    checkboxDisable: boolean;
    expanded: boolean;
    cats: any[];
    tabs = ['组合方案 1'];
    selected = new FormControl(0);

    canvas: boolean;
    serchControl =  new FormControl();
    filteredItems: Observable<any[]>;

    solutions: MySolution[];
    rawSolutions: Solution[];
    currentSolution: MySolution;
    // map from node id to its nodes
    inputPortsMap: any;
    outputPortsMap: any;

    matchingModels: any = [];
    defaultTab = false;
    currentNode = {
        name: '',
        authorName: '',
        version: '',
        modelType: '',
        toolkitType: '',
        modifiedDate: '',
        company: '',
        id: '',
        uuid: ''
    };
    modelMethodMap: any;
    constructor(
        private principal: Principal,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
        private solutionService: SolutionService,
        private dialog: MatDialog,
        private router: Router,
        private ochestratorService: OchestratorService,
        private documentService: DocumentService,
        private downloadService: DownloadService,
        private compositeSolutionService: CompositeSolutionService
    ) {
        this.filteredItems = this.serchControl.valueChanges
            .pipe(
                startWith(''),
                map((state) => state ? this._filterStates(state) : this.categoryNames.slice())
            );
    }

    ngOnInit() {
        this.cats = ['1', '2', '3'];
        this.expanded = false;
        this.currentUser = this.principal.getCurrentAccount();
        this.userLogin = this.currentUser.login;
        this.categoryNames = [{ name: '分类', expand: false, nodes: []},
            { name: '预测', expand: false, nodes: []},
            { name: '回归', expand: false, nodes: []},
            { name: '其他', expand: false, nodes: [] }];
        this.checkboxDisable = false;
        this.canvas = true;

        this.expanded = false;
        this.solutions = [];

        this.inputPortsMap = new Map();
        this.outputPortsMap = new Map();
        this.matchingModels = [];
        this.modelMethodMap = new Map();
        this.loadUnPublishedCompositeSolutions();
    }

    loadCurrentNode(e) {
        const queryOptions = {
            uuid: e
        };
        this.solutionService.query(queryOptions).subscribe((res) => {

            res.body.forEach((s) => {
                this.currentNode.name = s.name;
                this.currentNode.modelType = s.modelType;
                this.currentNode.version = s.version;
                this.currentNode.authorName = s.authorName;
                this.currentNode.toolkitType = s.toolkitType;
                this.currentNode.company = s.company;
                this.currentNode.uuid = s.uuid;
            });
        });
    }

    viewSolution(solution) {
        this.router.navigate(['/ucumos/solution/' + solution.uuid + '/' + 'view']);
    }

    loadPublishedSolutions() {
        const queryOptions = {
            active: true,
            publishStatus: '上架',
            subject3: 'base',
            size: 999999,
        };
        this.solutionService.query(queryOptions).subscribe((res) => {
            this.rawSolutions = res.body;
           if (this.solutions.length > 0) {
               this.currentSolution = this.solutions[0];
           }

           res.body.forEach((s) => {
               this.setSolutionModelMethod(s.uuid);
               this._fillCategories(s);
           });
        });
    }
    loadUnPublishedCompositeSolutions() {
        const queryOptions = {
            active: true,
            publishStatus: '下架',
            toolkitType: '模型组合',
            size: 999999,
            authorLogin: this.currentUser.login,
        };
        this.solutionService.query(queryOptions).subscribe((res) => {
            this.tabs = [];
            res.body.forEach((s) => {
                this._fillCompositeSolution(s);
            });
            this.loadPublishedSolutions();
            if (res.body.length === 0) {
                this.defaultTab = true;
                this.addDefaultTab();
            }
        });
    }
    private _fillCompositeSolution(s: Solution): void {
        if (s.toolkitType === '模型组合') {
            const sub: MySolution = new MySolution();
            sub.uuid = s.uuid;
            sub.name = s.name;
            sub.workflow = {};
            sub.activeLine = null;
            sub.activeLineGroup = null;
            sub.points = [];
            sub.translate = null;
            sub.drawLine = false;
            sub.dx = 0;
            sub.dy = 0;
            sub.dragElem = null;
            sub.nodes = [];
            sub.paths = [];
            sub.curPath = new MyPath();
            const data: any = {};
            data.solutionId = sub.uuid;
            data.login = s.authorLogin;

            this.ochestratorService.getCompositeSolutionGraphs(data).subscribe((resGraph) => {
                if (resGraph.body.nodes) {
                    resGraph.body.nodes.forEach((node) => {
                        const myNode: MyNode = new MyNode();
                        myNode.id = node.nodeId;
                        myNode.nodeSolutionId = node.nodeSolutionId;
                        myNode.text = node.name;
                        myNode.x = node.ndata.px;
                        myNode.y = node.ndata.py;
                        myNode.type = node.type;
                        myNode.inputCircles = [];
                        myNode.outputCircles = [];
                        sub.nodes.push(myNode);
                    });

                    if (resGraph.body.relations) {
                        resGraph.body.relations.forEach((relation) => {
                            const myPath: MyPath = new MyPath();
                            myPath.from = relation.sourceNodeId;
                            myPath.fromName = relation.sourceNodeName;
                            myPath.id = relation.linkId;
                            myPath.name = relation.linkName;
                            myPath.to = relation.targetNodeId;
                            myPath.toName = relation.targetNodeName;
                            myPath.start = relation.start;
                            myPath.end = relation.end;
                            myPath.input = relation.input;
                            myPath.output = relation.output;
                            sub.paths.push(myPath);
                        });
                    }
                }
                this.solutions.push(sub);
                this.tabs.push(sub.name);
                // this.updateResource();
            });
        }
    }
    private _fillCategories(s: Solution): void {

        this.categoryNames.forEach((c) => {
            const node: any = {};
            node.name = s.name;
            node.id = 'id' + s.uuid;
            node.nodeSolutionId = s.uuid;
            node.type = s.modelType != null ? s.modelType : '其他';
            switch (s.modelType) {
                case '分类':
                    if (c.name === '分类') {
                        c.nodes.push(node);
                    }
                    break;
                case '预测':
                    if (c.name === '预测') {
                        c.nodes.push(node);
                    }
                    break;
                case '回归':
                    if (c.name === '回归') {
                        c.nodes.push(node);
                    }
                    break;
                default:
                    if (c.name === '其他') {
                        c.nodes.push(node);
                    }
            }});

    }
    private _filterStates(value: string): any[] {
        const filterValue = value.toLowerCase();
        const result = [];
        this.categoryNames.forEach((item) => {
            const tmp: any = {};
            const t_nodes = item.nodes.filter((c) =>  c.name.toLowerCase().indexOf(filterValue) >= 0 );
            if (t_nodes.length > 0) {
                tmp.name = item.name;
                tmp.id = item.id;
                tmp.expand = item.expand;
                tmp.nodes = t_nodes;
                result.push(tmp);
            } else {
                if (item.name.toLowerCase().indexOf(filterValue) === 0) {
                    tmp.name = item.name;
                    tmp.id = item.id;
                    tmp.expand = item.expand;
                    tmp.nodes = item.nodes;
                    result.push(tmp);
                }
            }

        });
        return result;
    }
    addDefaultTab() {
        const sname = '......';
        const sol: any = {};
        sol.name = sname;
        sol.authorLogin = this.currentUser.login;
        sol.version = '1';
        sol.summary = '1';
        this.compositeSolutionService.createCompositeSolution(sol).subscribe((res) => {
            sol.uuid = res.body.uuid;
            console.log(res);
            this.addDefaultSolution(sol);
        });
    }
    addDefaultSolution(s: any) {
        this.tabs.push(s.name);
        this.currentSolution = new MySolution();
        this.currentSolution.name = s.name;
        this.currentSolution.uuid = s.uuid;
        this.currentSolution.version = s.version;
        this.currentSolution.description = s.description;
        this.currentSolution.activeLineGroup = null;
        this.currentSolution.activeLine = null;
        this.currentSolution.points = [];
        this.currentSolution.translate = null;
        this.currentSolution.drawLine = false;
        this.currentSolution.dx = 0;
        this.currentSolution.dy = 0;
        this.currentSolution.dragElem = null;
        this.currentSolution.nodes = [];
        this.currentSolution.paths = [];
        this.currentSolution.curPath = new MyPath();
        this.solutions.push(this.currentSolution);
        this.selected.setValue(this.tabs.length - 1);
    }
    addTab(selectAfterAdding: boolean) {
        const dialogRef = this.dialog.open(AddSolutionComponent, {
            width: '450px',
            data: {
                fromUserLogin: this.currentUser.login,
            },
        });
        dialogRef.afterClosed().subscribe((s) => {
            if (!s) {
                return;
            }
            this.addSolution(s, selectAfterAdding);
        });
        this.newCompositionButton = true;
        this.clearCompositeSolutionButton = false;
        this.closeCompositeSolutionButton = false;
    }
    addSolution(s: any, selectAfterAdding: boolean) {
        this.tabs.push(s.name);
        this.currentSolution = new MySolution();
        this.currentSolution.workflow = {
        };
        this.currentSolution.name = s.name;
        this.currentSolution.uuid = s.uuid;
        this.currentSolution.version = s.version;
        this.currentSolution.description = s.description;
        this.currentSolution.workflow.nodes = {}; // 统计各种节点的数目
        this.currentSolution.activeLineGroup = null;
        this.currentSolution.activeLine = null;
        this.currentSolution.points = [];
        this.currentSolution.translate = null;
        this.currentSolution.drawLine = false;
        this.currentSolution.dx = 0;
        this.currentSolution.dy = 0;
        this.currentSolution.dragElem = null;
        this.currentSolution.nodes = [];
        this.currentSolution.paths = [];
        this.currentSolution.curPath = new MyPath();
        this.solutions.push(this.currentSolution);
        if (selectAfterAdding) {
            this.selected.setValue(this.tabs.length - 1);
        }
        this.currentNode.name = '';
        this.currentNode.modelType = '';
        this.currentNode.version = '';
        this.currentNode.authorName = '';
        this.currentNode.toolkitType = '';
        this.currentNode.company = '';
        this.currentNode.uuid = '';
    }

    removeTab(index: number) {
        // if (this.tabs.length === 1) {
        //     this.snackBarService.error('唯一的组合方案不能删除');
        //     return;
        // }
        this.confirmService.ask('确定要删除该组合方案？').then((confirm) => {
            if (confirm) {
                const data: any = {};
                data.sol = this.userLogin;
                this.compositeSolutionService.deleteCompositeSolution(this.solutions[index].uuid).subscribe((res) => {
                        this.snackBarService.success('删除组合方案成功!');
                        this.tabs.splice(index, 1);
                        this.solutions.splice(index, 1);
                    },
                    (error) => {
                        this.snackBarService.error('删除组合方案失败!' + error);
                    });
            }
        });
        // this.tabs.splice(index, 1);
        // this.solutions.splice(index, 1);
        // if (index == this.selected.value) {
        //     if(th)
        // }
        // this.currentSolution = this.solutions[this.selected.value];
    }

    changeSolution(e) {
        this.currentSolution = this.solutions[e];
    }
    clearCanvas() {
        select('svg').selectAll('path.cable').data([]).exit().remove();
        select('svg').selectAll('g').data([]).exit().remove();
    }
    updateResource() {
        this.currentSolution = this.solutions[this.selected.value];
        this.clearCanvas();
        this.updateNode(this.currentSolution.nodes);
        // this.currentSolution.nodes = [];
        this.updatePath(this.currentSolution.paths);
        this.currentSolution.nodes.forEach((node: MyNode) => {
            this.updateCable(select('#' + node.id));
        });
        // this.updatePath(this.currentSolution.paths);
    }

    updatePath(paths: MyPath[]) {
        // const t =  select('svg').selectAll('path.cable').data([]);
        // const t2 =  select('svg').selectAll('path .cable').data([]);

        select('svg').selectAll('g.path').data(paths).enter()
            .append('g')
            .attr('class', 'path')
            .append('path')
            .attr('id', (path) =>  path.id)
            .attr('class', 'cable')
            .attr('stroke', '#333')
            .attr('text', function(d) {return d.name ? d.name : ''; })
            .attr('stroke-width', '2px')
            .attr('fill', 'none')
            .attr('from', (path) => path.from)
            .attr('start', (path) => path.start)
            .attr('output', (path) => path.output)
            // .attr('output', d.number)
            .attr('marker-end', 'url(#arrowhead)')
            .attr('d', (path) => path.d)
            .attr('to', (path) => path.to)
            .attr('input', (path) => path.input)
            .attr('end', (path) => path.end)
            .attr('cursor', 'pointer')
            // .on('dblclick', (path) => { this.removePath(path); });
            .on('click', (path) => {
                this.pathClicked = true;
                this.pathClickedObject = path;
            });

        selectAll('g.path').append('text').attr('id', (d: any) => {
            return 'pathText' + d.id;
        });
        selectAll('path.cable').each(function(d: any) {
                const start: any = select(this).attr('start').split(',');
                start[0] = toInt(start[0]);
                start[1] = toInt(start[1]);

                const path = d.d;
                let end: any;
                if (path) {
                    end = path.substring(path.lastIndexOf(' ') + 1).split(',');
                }else {
                    end = select(this).attr('end').split(',');
                }
                end[0] = toInt(end[0]);
                end[1] = toInt(end[1]);

                // select(this).attr('d', function() {
                //     return 'M' + start[0] + ',' + start[1]
                //         + ' C' + start[0] + ',' + (start[1] + end[1]) / 2
                //         + ' ' + end[0] + ',' +  (start[1] + end[1]) / 2
                //         + ' ' + end[0] + ',' + end[1];
                // });
                const pathId =  d.id;
                let x = (end[0] - start[0]) / 2;
                let y = (end[1] - start[1]) / 2;
                x = x > 0 ? x : -x;
                y = y > 0 ? y : -y;
                const text = select('#pathText' + pathId).select('textPath');
                if (text.empty()) {
                    select('#pathText' + pathId).attr('x', x).attr('y', y)
                        .append('textPath').attr('xlink:href', '#' + pathId).text(d.name);
                }
            });
        // paths.forEach((path) => {
        //     const Line = select('svg')
        //         .append('path')
        //         .attr('class', 'cable')
        //         .attr('stroke', '#333')
        //         .attr('stroke-width', '2px')
        //         .attr('fill', 'none')
        //         .attr('from', path.from)
        //         .attr('start', path.start)
        //         .attr('output', path.output)
        //         // .attr('output', d.number)
        //         .attr('marker-end', 'url(#arrowhead)')
        //         .attr('d', path.d)
        //         .attr('to', path.to)
        //         .attr('input', path.input)
        //         .attr('end',path.end);
        // });

         selectAll('g.path').selectAll('textPath').on('click', (p) => this.editeLink(p));
    }

    save() {
        if (this.defaultTab || this.currentSolution.name === '......') {
            const dialogRef = this.dialog.open(UpdateSolutionComponent, {
                width: '450px',
                data: {
                    fromUserLogin: this.currentUser.login,
                    uuid: this.currentSolution.uuid,
                },
            });
            dialogRef.afterClosed().subscribe((s) => {
                if (!s) {
                    return;
                }
                this.tabs = [];
                this.tabs.push(s.name);
                this.currentSolution.name = s.name;
                this.updateSolutionCdump();
            });
            this.defaultTab = false;
        }else {
            this.updateSolutionCdump();
        }
    }
    updateSolutionCdump() {
        const body: Solution = new Solution();
        body.uuid = this.currentSolution.uuid;
        body.authorLogin = this.currentUser.login;
        body.name = this.currentSolution.name;
        body.version = this.currentSolution.version;
        this.compositeSolutionService.saveCompositeSolutionCdump(body).subscribe((res) => {
                console.log(res);
                this.snackBarService.success('保存组合方案成功!');
            },
            (error) => {
                this.snackBarService.error('保存组合方案失败!' + error);
            });
    }
    validate() {
        const body: Solution = new Solution();
        body.uuid = this.currentSolution.uuid;
        body.authorLogin = this.currentUser.login;
        body.name  = this.currentSolution.name;
        body.version = this.currentSolution.version;
        this.compositeSolutionService.validateCompositeSolution(body).subscribe((res) => {
                console.log(res);
                this.snackBarService.success('校验方案成功!');
            },
            (error) => {
                this.snackBarService.error('校验组合方案失败!' + error);
            });
    }
    // newSolution(ar) {
    //
    // }
    // setProbe(a) {
    //
    // }
    // showDeleteNodeLink() {
    //
    // }
    // clearSolution() {
    //
    // }

    dragElStart(e) {
        if (!this.currentSolution) {
            this.snackBarService.warning('请先创建组合解决方案');
            return;
        }

        e.dataTransfer.setData('Text', e.target.id);
    }
    _getMatchingModels(portType: any, protobufJsonString: any) {
        const body: any = {};
        body.userId = this.currentUser.login;
        body.portType = portType;
        body.protobufJsonString = JSON.stringify(protobufJsonString[0]);
        this.ochestratorService.getMatchingModels(body).subscribe((res) => {
            if (res.body.matchingModels.length === 0) {
                this.snackBarService.error('no matching models for this port');
            }else {
                let i = 0;
                const matchingModels = [];
                res.body.matchingModels.forEach((value) => {
                    this.rawSolutions.forEach((value1) => {
                        if (value.matchingModelName === value1.name) {
                            if (matchingModels.indexOf(value1) === -1) {
                                matchingModels[i++] = value1;
                            }
                        }
                    });
                });
                this.matchingModels = matchingModels;
            }
        });
    }
    linestarted(d) {
        const id: string = '#' + d.id.toString();
        this.currentSolution.drawLine = false;
        // 当前选中的circle
        const anchor = select(id);
        if (anchor) {
            this._getMatchingModels(d.type, d.originalType);
        }
        // 当前选中的节点
        const node = select('#' + d.parentId);
        const rect = (node.node() as HTMLElement).getBoundingClientRect();
        const dx = d.number * rect.width / (toInt(node.attr('outputs'))  + 1);
        const dy = rect.height;
        const transform = node.attr('transform');
        this.currentSolution.translate = this.getTranslate(transform);
        this.currentSolution.points.push([dx + this.currentSolution.translate[0], dy + this.currentSolution.translate[1]]);

        const pathId =  'idpath' + new Date().getTime();
        this.currentSolution.curPath.id = pathId;
        this.currentSolution.curPath.from = node.attr('id');
        this.currentSolution.curPath.fromName = node.attr('name');
        this.currentSolution.curPath.start = dx + ', ' + dy;
        this.currentSolution.curPath.output = anchor.attr('output');
        this.currentSolution.paths.push(this.currentSolution.curPath);
        // this.currentSolution.activeLine = select('svg').selectAll('path.cable').data(this.currentSolution.paths).enter()
        this.currentSolution.activeLineGroup = select('svg').selectAll('g.path').data(this.currentSolution.paths).enter()
            .append('g')
            .attr('class', 'path');
        this.currentSolution.activeLine = this.currentSolution.activeLineGroup
            .append('path')
            .attr('class', 'cable')
            .attr('stroke', '#333')
            .attr('stroke-width', '2px')
            .attr('fill', 'none')
            // .attr('from', node.attr('id'))
            .attr('from', (path) => path.from)
            .attr('start', (path) => path.start)
            .attr('output', (path) => path.output)
            .attr('id', (path) => path.id)
            // .attr('output', d.number)
            .attr('marker-end', 'url(#arrowhead)');
    }

    linedragged() {
        this.currentSolution.drawLine = true;
        this.currentSolution.points[1] = [event.x + this.currentSolution.translate[0], event.y + this.currentSolution.translate[1]];
        this.currentSolution.curPath.d =  'M' + this.currentSolution.points[0][0] + ',' + this.currentSolution.points[0][1]
            + 'C' + this.currentSolution.points[0][0] + ',' + (this.currentSolution.points[0][1] + this.currentSolution.points[1][1]) / 2
            + ' ' + this.currentSolution.points[1][0] + ',' +  (this.currentSolution.points[0][1] + this.currentSolution.points[1][1]) / 2
            + ' ' + this.currentSolution.points[1][0] + ',' + this.currentSolution.points[1][1];
        this.currentSolution.activeLine.attr('d', (d) => d.d);
    }

    _checkModelMatch(name: string) {
        let res = false;
        this.matchingModels.forEach((m) => {
            if (m.name === name) {
                res = true;
            }
        });
        return res;
    }

    lineEnded(d: any) {
        this.currentSolution.drawLine = false;
        const anchor = selectAll('circle.end');
        if (anchor.empty()) {
            this.currentSolution.activeLine.remove();
            this.currentSolution.paths.pop();
            // select('svg').selectAll('path.cable').data(this.currentSolution.paths).exit().remove();
            select('svg').selectAll('g.path').data(this.currentSolution.paths).exit().remove();
        } else {
            const pNode = select((anchor.node() as any).parentNode);
            if (!this._checkModelMatch(pNode.attr('name'))) {
                anchor.classed('end', false);
                this.currentSolution.activeLine.remove();
                this.currentSolution.paths.pop();
                this.snackBarService.error('this model is not a match');
            }else {
                const input = toInt(anchor.attr('input')) * (pNode.node() as any).getBoundingClientRect().width / (toInt(pNode.attr('inputs')) + 1);
                anchor.classed('end', false);
                this.currentSolution.curPath.to = pNode.attr('id');
                this.currentSolution.curPath.toName = pNode.attr('name');
                this.currentSolution.curPath.input = anchor.attr('input');
                this.currentSolution.curPath.end = input + ', 0';
                this.currentSolution.activeLine.attr('to', (path) => path.to);
                this.currentSolution.activeLine.attr('input', (path) => path.input);
                this.currentSolution.activeLine.attr('end', (path) => path.end);
                this.currentSolution.activeLine.attr('cursor', 'pointer');
                // this.currentSolution.activeLine.on('dblclick', (path) => { this.removePath(path); });
                this.currentSolution.activeLine.on('click', (path) => {
                    this.pathClicked = true;
                    this.pathClickedObject = path;
                });
                // this.currentSolution.activeLine.on('click', (path) => { this.editeLink(path); });
                this._addLink();
            }
        }
        this._cleanPath();
    }
    _addLink() {
        const curPath = this.currentSolution.curPath;
        const pathGroup = this.currentSolution.activeLineGroup;
        const points = this.currentSolution.points;
        const dialogRef = this.dialog.open(OchestratorEditeLinkComponent, {
            width: '450px',
            data: {
                linkName: null
            },
        });
        dialogRef.afterClosed().subscribe((pathName) => {
            if (!pathName) {
                return;
            }
            curPath.name = pathName;
            // 向umo发送请求创建链路
            this.addLinkToRemote(curPath).subscribe(() => {

                let x = (points[1][0] - points[0][0]) / 2;
                let y = (points[1][1] - points[0][1]) / 2;
                x = x > 0 ? x : -x;
                y = y > 0 ? y : -y;
                pathGroup.append('text').attr('x', x).attr('y', y).attr('id' , 'pathText' + curPath.id)
                    .append('textPath').attr('xlink:href', '#' + curPath.id).text(curPath.name).on('click', (p) => this.editeLink(p));
                this.snackBarService.success('创建链路成功');
            }, () => {
                this.snackBarService.error('创建链路失败');
                select('#' + curPath.id).remove();
                const index = this.currentSolution.paths.findIndex((e) => {
                    // return e.from === path.from && e.to === path.to;
                    return e.id === curPath.id;
                });
                this.currentSolution.paths.splice(index, 1);
            }, () => {
            });
        });
    }

    _cleanPath() {
        this.currentSolution.curPath = new MyPath();
        this.currentSolution.activeLineGroup = null;
        this.currentSolution.activeLine = null;
        this.currentSolution.points = [];
        this.currentSolution.translate = null;
    }

    removePath() {

        this.confirmService.ask('确定要删除链路?').then((confirm) => {
            if (confirm) {
                this.ochestratorService.deleteLink(this.currentSolution.uuid, this.currentUser.login, this.pathClickedObject.id).subscribe((res) => {
                        const index = this.currentSolution.paths.findIndex((e) => {
                            // return e.from === path.from && e.to === path.to;
                            return e.id === this.pathClickedObject.id;
                        });
                        this.currentSolution.paths.splice(index, 1);
                        select('#' + this.pathClickedObject.id).remove();
                        this.snackBarService.success('删除链路成功');
                },
                    (err) => {
                    this.snackBarService.error('删除链路失败');
                    }
                );
            }
        });
        this.pathClicked = false;
    }

    getTranslate(transform) {
        const arr = transform.substring(transform.indexOf('(') + 1, transform.indexOf(')')).split(',');
        return [+arr[0], +arr[1]];
    }

    dragStarted(e) {
        const id: string = '#' + e.id.toString();
        this.currentSolution.dragElem = select(id);
        const transform = this.currentSolution.dragElem .attr('transform');
        const translate = this.getTranslate(transform);
        this.currentSolution.dx = event.x - translate[0];
        this.currentSolution.dy = event.y - translate[1];
        this.currentSolution.dragElem = select(id);
        const uuid: string = e.nodeSolutionId;
        this.loadCurrentNode(uuid);
        // e.call(zoom().scaleExtent([1/2, 4])
        //     .on('zoom'), this.currentSolution.dragElem .attr('transform'));
        }

    dragged(e) {
        const bk = select('#svgcanvas');
        const bound = (bk.node() as HTMLElement).getBoundingClientRect();
        const width = bound.width - 180;
        const height = bound.height - 100;
        let dx = event.x - this.currentSolution.dx;
        let dy = event.y - this.currentSolution.dy;
        if ( dx < 0) {
            dx = 0;
        }
        if ( dy < 0) {
            dy = 0;
        }
        dx = dx > width ? width : dx;
        dy = dy > height ? height : dy;
        this.currentSolution.dragElem.attr('transform', 'translate(' + dx + ', ' + dy + ')');
        const id = this.currentSolution.dragElem.attr('id');
        const node: any = this.currentSolution.nodes.find((n) => {
            return id === n.id;
        });
        node.x = dx;
        node.y = dy;
        this.updateCable(this.currentSolution.dragElem);
    }

    updateCable(elem) {
        // const bound = elem.node().getBoundingClientRect();
        // const width = bound.width;
        // const height = bound.height;
        const id = elem.attr('id');
        const transform = elem.attr('transform');
        const t1 = this.getTranslate(transform);
        const fromStr: string = 'path[from=' + id + ']';
        const fromTo: string = 'path[to=' + id + ']';
        // 更新输出线的位置
        const from = selectAll(fromStr);
        from.each(function(d) {
            const start: any = select(this).attr('start').split(',');
            start[0] = toInt(start[0]) + t1[0];
            start[1] = toInt(start[1]) + t1[1];

            const path = select(this).attr('d');
            let end: any;
            if (path) {
                end = path.substring(path.lastIndexOf(' ') + 1).split(',');
            }else {
                end = select(this).attr('end').split(',');
            }

            end[0] = toInt(end[0]);
            end[1] = toInt(end[1]);

            select(this).attr('d', function() {
                return 'M' + start[0] + ',' + start[1]
                    + ' C' + start[0] + ',' + (start[1] + end[1]) / 2
                    + ' ' + end[0] + ',' +  (start[1] + end[1]) / 2
                    + ' ' + end[0] + ',' + end[1];
            });

            const pathId =  select(this).attr('id');
            let x = (end[0] - start[0]) / 2;
            let y = (end[1] - start[1]) / 2;
            x = x > 0 ? x : -x;
            y = y > 0 ? y : -y;
            if (path) {
                select('#pathText' + pathId).attr('x', x).attr('y', y);
            }
        });

        // 更新输入线的位置
        const toVar = selectAll(fromTo);
        toVar.each(function(d) {
            const path = select(this).attr('d');
            let start: any;
            if (path) {
                start = path.substring(1, path.indexOf('C')).split(',');
            }else {
                start = select(this).attr('start').split(',');
            }
            start[0] = toInt(start[0]);
            start[1] = toInt(start[1]);

            const end: any = select(this).attr('end').split(',');
            end[0] = toInt(end[0]) + t1[0];
            end[1] = toInt(end[1]) + t1[1];

            select(this).attr('d', function() {
                return 'M' + start[0] + ',' + start[1]
                    + ' C' + start[0] + ',' + (start[1] + end[1]) / 2
                    + ' ' + end[0] + ',' +  (start[1] + end[1]) / 2
                    + ' ' + end[0] + ',' + end[1];
            });
            const pathId =  select(this).attr('id');
            let x = (end[0] - start[0]) / 2;
            let y = (end[1] - start[1]) / 2;
            x = x > 0 ? x : -x;
            y = y > 0 ? y : -y;
            if (path) {
                select('#pathText' + pathId).attr('x', x).attr('y', y);
            }
        });

    }

    dragEnded(e) {
        this.currentSolution.dx = this.currentSolution.dy = 0;
        this.currentSolution.dragElem = null;
        // $('moveNode').removeClass('moveNode');
    }
    // initSVGElement() {
    //     this.currentSolution.nodes = [];
    //     this.currentSolution.gNodes = selectAll('g').data(this.currentSolution.nodes);
    // }
    //
    // _fillPorts() {
    //
    // }
    updateNode(nodes: MyNode[]) {
        const svg = select('svg');
        svg.selectAll('g').data(this.currentSolution.nodes).exit().remove();
        let i = 0;
        nodes.forEach((node: MyNode) => {
            let inputPorts = this.inputPortsMap.get(node.id);
            let outputPorts = this.outputPortsMap.get(node.id);
            // if ports is null, then get port from server side
            if ( inputPorts == null && outputPorts == null) {
                const body: any = {};
                body.solutionId = node.nodeSolutionId;
                this.ochestratorService.getTgif(body).subscribe((tgif) => {
                    const requirementJson = [], capabilityJson = [];
                    // get requirements
                    let check_isValid_calls = '';
                    let check_isValid_provides = '';

                    tgif.body.services.calls.forEach((value) => {
                        if (value.request.format.length !== 0) {
                            check_isValid_calls = value.request.format[0].messageName;
                            const reqObj = value.request.format;
                            const reqOperation = value.config_key;
                            requirementJson.push({
                                    'name': '',
                                    'relationship': '',
                                    'id': '',
                                    'capability': {
                                        'name': reqObj,
                                        'id': reqOperation
                                    },
                                    'target': {'name': '', 'description': ''},
                                    'target_type': 'Node'
                                }
                            );
                        }
                    });
                    // get capabilities
                    tgif.body.services.provides.forEach((value) => {
                        if (value.request.format.length !== 0) {
                            check_isValid_provides = value.request.format[0].messageName;
                            const capObj = value.request.format;
                            const capOperation = value.route;
                            capabilityJson.push({
                                'id': '',
                                'name': '',
                                'target': {
                                    'name': capObj,
                                    'id': capOperation
                                },
                                'target_type': 'Capability',
                                'properties': null
                            });
                        }
                    });
                    requirementJson.forEach((req) => {
                        req.capability.name = this.removeMsgNames(req.capability.name);
                        req.type = this.is_wildcard_type(req.capability.name) ? null : JSON.stringify(req.capability.name);
                    });
                    capabilityJson.forEach((cap) => {
                        cap.target.name = this.removeMsgNames(cap.target.name);
                        cap.type = this.is_wildcard_type(cap.target.name) ? null : JSON.stringify(cap.target.name);
                    });
                    const def = {
                        'extras': []
                    };
                    inputPorts = this._buildInputPorts(node.id, node.type, requirementJson, def.extras);
                    // this.inputPortsMap.set(node.nodeSolutionId, inputPorts);
                    this.inputPortsMap.set(node.id, inputPorts);
                    outputPorts = this._buildOutputPorts(node.id, node.type, capabilityJson, def.extras);
                    // this.outputPortsMap.set(node.nodeSolutionId, outputPorts);
                    this.outputPortsMap.set(node.id, outputPorts);
                    if ( node.inputCircles.length === 0 ) {
                        inputPorts.forEach((port) => {
                            node.inputCircles.push(port);
                        });
                    }
                    if ( node.outputCircles.length === 0 ) {
                        outputPorts.forEach((port) => {
                            node.outputCircles.push(port);
                        });
                    }
                    this.addNodeToCanvas(svg, node);
                    i++;
                    if (i === nodes.length) {
                        this.updatePath(this.currentSolution.paths);
                        this.currentSolution.nodes.forEach((n: MyNode) => {
                            this.updateCable(select('#' + n.id));
                        });
                    }
                });
            }else {
                if ( node.inputCircles.length === 0 ) {
                    inputPorts.forEach((port) => {
                        node.inputCircles.push(port);
                    });
                }
                if ( node.outputCircles.length === 0 ) {
                    outputPorts.forEach((port) => {
                        node.outputCircles.push(port);
                    });
                }
                this.addNodeToCanvas(svg, node);
                i++;
                if (i === nodes.length) {
                    this.updatePath(this.currentSolution.paths);
                    this.currentSolution.nodes.forEach((n: MyNode) => {
                        this.updateCable(select('#' + n.id));
                    });
                }
            }
        });

    }
    addNodeToCanvas(svg: any, node: MyNode) {
        const uuid: string = node.nodeSolutionId;
        this.loadCurrentNode(uuid);
        // this.currentSolution.nodes
        const nodes: MyNode[] = [];
        nodes.push(node);
        // const gNodes = svg.selectAll('g.node').data(nodes);
        const gNodes = svg.selectAll('g.node').data(nodes, (d) => {
            return d.id;
        });
        // // 计算节点编号
        const g = gNodes.enter().append('g')
            .attr('class', 'node')
            .attr('data-id', function(d) {return d.nodeSolutionId; })
            .attr('id', function(d) {
                return d.id;
            })
            .attr('name', function(d) {
                return d.text;
            })
            .attr('transform', function(d) {
                return 'translate(' + d.x + ', ' + d.y + ')';
            });
        const rect = g.append('rect')
            .attr('rx', 6)
            .attr('ry', 6)
            .attr('stroke-width', 2)
            .attr('stroke', '#999')
            .attr('fill', '#fff')
            .attr('width', '180px')
            .attr('height', '36px')
            .attr('cursor', 'pointer');
        if (!rect.node()) {
            return g;
        }
        const bound = (rect.node() as HTMLElement).getBoundingClientRect();
        const width = bound.width;
        const height = bound.height;
        // text
        g.append('text')
            .text(function(d: MyNode) {
                return d.text;
            })
            .attr('x', width / 2)
            .attr('y', height / 2)
            .attr('dominant-baseline', 'central')
            .attr('text-anchor', 'middle');

        // left icon
        g.append('text')
            .attr('x', 18)
            .attr('y', height / 2)
            .attr('dominant-baseline', 'central')
            .attr('text-anchor', 'middle')
            .attr('font-family', 'FontAwesome')
            .text('\uf1c0');

        // right icon
        g.append('text')
            .attr('x', width - 18)
            .attr('y', height / 2)
            .attr('dominant-baseline', 'central')
            .attr('text-anchor', 'middle')
            .attr('font-family', 'FontAwesome')
            .text('\uf00c');

        // input circle
        const inputs = node.inputCircles.length || 0;
        g.attr('inputs', inputs);

        g.selectAll('circle.input').data(node.inputCircles).enter().append('circle')
            .attr('class', 'input')
            .attr('input', function(d) {
                return d.number;
            })
            .attr('id', function(d) {
                return d.id;
            })
            .attr('cx', function(d) {
                return width * (d.number) / (inputs + 1);
            })
            .attr('cy', 0)
            .attr('r', 6)
            .attr('stroke', '#33cc33')
            .attr('stroke-width', '3px')
            .attr('cursor', 'pointer')
            // .attr('fill', '#fff');
            .attr('fill', '#efefef');

        // output circle
        const outputs = node.outputCircles.length || 0;
        g.attr('outputs', outputs);
        g.selectAll('circle.output').data(node.outputCircles).enter().append('circle')
            .attr('class', 'output')
            .attr('output', function(d) {
                return d.number;
            })
            .attr('id', function(d) {
                return d.id;
            })
            .attr('cx', function(d) {
                return width * (d.number) / (outputs + 1);
            } )
            .attr('cy', height)
            // .attr('r', 5)
            // .attr('stroke-width', '2px')
            // .attr('cursor', 'pointer');
            .attr('r', 6)
            .attr('stroke', '#cc3333')
            .attr('stroke-width', '3px')
            .attr('cursor', 'pointer')
            .attr('fill', '#223397');

        g.call(
            drag()
                .on('start', (d) => {this.dragStarted(d); })
                .on('drag', (d) => {this.dragged(d); })
                .on('end', (d) => {this.dragEnded(d); })
        );
        g.selectAll('rect').on('dblclick', (d) => {
            this.ochestratorService.deleteNode(this.currentSolution.uuid, this.currentUser.login, d.id).subscribe(() => {
                const index = this.currentSolution.nodes.findIndex((e) => {
                    return e.id === d.id;
                });
                this.currentSolution.nodes.splice(index, 1);
                select('#' + d.id).remove();
                const paths = [];
                this.currentSolution.paths.forEach((e) => {
                    if (e.from !== d.id && e.to !== d.id) {
                        paths.push(e);
                    }
                });
                this.currentSolution.paths = paths;
                svg.selectAll('path.cable').data(paths).exit().remove();
                this.snackBarService.success('删除节点成功');
            },
                () => {
                this.snackBarService.error('删除节点失败');
            });
            // 更新输出线的位置
        });
        g.selectAll('rect').on('click', (d) => {
            this.clickLink(d);
        });

        g.selectAll('circle.output').call(
            drag()
                .on('start', (d) => { this.linestarted(d); })
                .on('drag', (d) => { this.linedragged(); })
                .on('end', (d) => { this.lineEnded(d); })
        );
        // set current circle.input class 'end' with true and all circle.end class 'end' with false
        g.selectAll('circle.input')
            .on('mouseover', (d) => {
                if (this.currentSolution.drawLine) {
                    selectAll('circle.end').classed('end', false);
                    select('#' + d.id).classed('end', true);
                }
            });

        return g;
    }

    dropEl(e) {
        e.preventDefault();
        const objId = e.dataTransfer.getData('Text');

        // 防止对线段的拖拽
        if (!objId.startsWith('id')) {
            return;
        }
        const el1 = select('#idsw-bpmn').node()  as HTMLElement;
        const b = el1.getBoundingClientRect();
        const el = <HTMLElement>document.getElementById('' + objId).lastChild;
        const dx: any = e.clientX - b.left;
        // x = toInteger(x.replace('px','')) - 250;
        const dy: any = e.clientY - b.top;
        // y = toInteger(y.replace('px','')) - 40;
        const node: MyNode = {
            id: 'id' + new Date().getTime(),
            nodeSolutionId: objId.substring(2),
            // id: objId,
            // nodeSolutionId: toInteger(new Date().getTime()),
            x: dx,
            y: dy,
            text: el.textContent,
            inputCircles: [],
            outputCircles: [],
            type: ''
        };
        node.type = this._getNodeType(node.nodeSolutionId);

        const requirementJson = [], capabilityJson = [];
        /*
        syntax = "proto3";package eUKXJBjgoRdRsGWLlmxMWzqHUHkQwoat;
        service Model {rpc classify (IrisDataFrame) returns (ClassifyOut);}
        message ClassifyOut {repeated int64 value = 1;}
        message IrisDataFrame {
          repeated double sepal_length = 1;
          repeated double sepal_width = 2;
          repeated double petal_length = 3;
          repeated double petal_width = 4;}
         */
        /*
        {"self":{"version":"snapshot","name":"tf-model","description":"","component_type":"Docker"},"streams":{},"services":
        {"calls":[{"config_key":"classify","request":{"format":[{"messageName":"ClassifyOut","messageargumentList":
        [{"role":"repeated","name":"value","tag":"1","type":"int64"}]}],"version":""},"response":{"format":[],"version":""}}],"provides":
        [{"route":"classify","request":{"format":[{"messageName":"IrisDataFrame","messageargumentList":
        [{"role":"repeated","name":"sepal_length","tag":"1","type":"double"},{"role":"repeated","name":"sepal_width","tag":"2","type":"double"},
        {"role":"repeated","name":"petal_length","tag":"3","type":"double"},{"role":"repeated","name":"petal_width","tag":"4","type":"double"}]}],"version":""},
        "response":{"format":[],"version":""}}]},"parameters":[],"auxiliary":{},"artifacts":[]}
         */
        const body: any = {};
        body.solutionId = node.nodeSolutionId;
        this.ochestratorService.getTgif(body).subscribe((tgif: any) => {
            // get requirements
            let check_isValid_calls = '';
            let check_isValid_provides = '';

            tgif.body.services.calls.forEach((value) => {
                if (value.request.format.length !== 0) {
                    check_isValid_calls = value.request.format[0].messageName;
                    const reqObj = value.request.format;
                    const reqOperation = value.config_key;
                    requirementJson.push({
                            'name': '',
                            'relationship': '',
                            'id': '',
                            'capability': {
                                'name': reqObj,
                                'id': reqOperation
                            },
                            'target': {'name': '', 'description': ''},
                            'target_type': 'Node'
                        }
                    );
                }
            });
            // get capabilities
            tgif.body.services.provides.forEach((value) => {
                if (value.request.format.length !== 0) {
                    check_isValid_provides = value.request.format[0].messageName;
                    const capObj = value.request.format;
                    const capOperation = value.route;
                    capabilityJson.push({
                        'id': '',
                        'name': '',
                        'target': {
                            'name': capObj,
                            'id': capOperation
                        },
                        'target_type': 'Capability',
                        'properties': null
                    });
                }
            });
            const def = {
                'id': tgif.body.self.name,
                'name': tgif.body.self.name,
                'ndata': {},
                'capabilities': capabilityJson,
                'requirements': requirementJson,
                'extras': []
            };
            this.addNodeToRemote(node, requirementJson, capabilityJson).subscribe((res) => {
                const svg = select('svg');
                let inputPorts = this.inputPortsMap.get(node.id);
                let outputPorts = this.outputPortsMap.get(node.id);
                if ( inputPorts == null || outputPorts == null) {
                    requirementJson.forEach((req) => {
                        req.capability.name = this.removeMsgNames(req.capability.name);
                        req.type = this.is_wildcard_type(req.capability.name) ? null : JSON.stringify(req.capability.name);
                    });
                    capabilityJson.forEach((cap) => {
                        cap.target.name = this.removeMsgNames(cap.target.name);
                        cap.type = this.is_wildcard_type(cap.target.name) ? null : JSON.stringify(cap.target.name);
                    });
                    inputPorts = this._buildInputPorts(node.id, node.type, def.requirements, def.extras);
                    // this.inputPortsMap.set(node.nodeSolutionId, inputPorts);
                    this.inputPortsMap.set(node.id, inputPorts);
                    outputPorts = this._buildOutputPorts(node.id, node.type, def.capabilities, def.extras);
                    // this.outputPortsMap.set(node.nodeSolutionId, outputPorts);
                    this.outputPortsMap.set(node.id, outputPorts);
                }
                if ( node.inputCircles.length === 0 ) {
                    inputPorts.forEach((port) => {
                        node.inputCircles.push(port);
                    });
                }
                if ( node.outputCircles.length === 0 ) {
                    outputPorts.forEach((port) => {
                        node.outputCircles.push(port);
                    });
                }
                this.currentSolution.nodes.push(node);
                this.addNodeToCanvas(svg, node);
            });

        });
    }
    // number 计算节点编号
    private _buildInputPorts(nid: any, ntype: any, requirements: any[], extras: any[]): any[] {
        return requirements.map(function(req , i){ return {
            id: 'idcin' + i.toString() + new Date().getTime(),
            number: i + 1,
            parentId: nid,
            nodeId: nid,
            // portname: req.capability.id + '+' + JSON.stringify(this.removeMsgNames(req.capability.name)) + '+req' + i,
            // type: this.is_wildcard_type(req.capability.name) ? null : JSON.stringify(this.removeMsgNames(req.capability.name)),
            portName: req.capability.id + '+' + JSON.stringify(req.capability.name) + '+req' + i,
            type: 'input',
            fullType: req.capability.name,
            originalType: req.capability.name,
            shortName: req.capability.id,
            nodeType: ntype
        };
        });
    }

    private _buildOutputPorts(nid: any, ntype: any, capabilities: any[], extras: any[]): any[] {
        return capabilities.map(function(cap , i) { return {
            id: 'idcout' + i.toString() + new Date().getTime(),
            number: i + 1,
            parentId: nid,
            nodeId: nid,
            portName: cap.target.id + '+' + JSON.stringify(cap.target.name) + '+cap' + i,
            type: 'output',
            fullType: cap.target.name,
            originalType: cap.target.name,
            shortName: cap.target.id,
            nodeType: ntype
        };
        });
    }
    removeMsgNames(msgType: any[]): any {
        return msgType.map(function(msg) {
            return msg.messageargumentList ? msg.messageargumentList.map(function(arg) {
                return {
                    role: arg.role,
                    tag: arg.tag,
                    type: arg.type
                };
            }) : [];
        });
    }

    is_wildcard_type(type: any[]): any {
        if (type[0].type !== 'script') {
            return type[0].messageName === 'ANY'; // replace with correct
        } else {
            return false;
        }
    }

    dragElenter(e) {
    }
    dragElover(e) {
        e.preventDefault();
    }
    dragElend(e) {
        e.preventDefault();
    }

    private _getNodeType(id: any): string {
        let t = '';
        this.categoryNames.forEach((c) => {
            c.nodes.forEach((n) => {
                if ( n.nodeSolutionId === id ) {
                    t = n.type;
                }
            });
        });
        return t;
    }

    addNodeToRemote(myNode: MyNode, requirementJson: any, capabilityJson: any): Observable<HttpResponse<any>> {

        const data: any = {};
        data.userId = this.currentUser.login;
        data.solutionId = this.currentSolution.uuid;
        data.name = this.currentSolution.name;
        const ndata: Ndata = new Ndata();
        ndata.px = myNode.x;
        ndata.py = myNode.y;
        ndata.fixed = true;

        const node: Node = new Node();
        node.name = myNode.text;
        node.nodeSolutionId = myNode.nodeSolutionId;
        node.ndata = ndata;
        node.nodeId = myNode.id;
        node.type = myNode.type;
        node.capabilities = capabilityJson;
        node.requirements = requirementJson;
        data.node = node;

        // node.nodeVersion =
       return this.ochestratorService.createNodes(data);
    }

    // addCompositeSolution() {
    //
    // }
    clearCompositeSolution() {
        this.confirmService.ask('确定要清空该组合方案？').then((confirm) => {
            if (confirm) {
                const data: any = {};
                data.authorLogin = this.currentUser.login;
                data.uuid = this.currentSolution.uuid;
                this.compositeSolutionService.clearCompositeSolution(data).subscribe(() => {
                    this.snackBarService.success('清空组合方案成功!');
                    this.currentSolution.paths = [];
                    this.currentSolution.nodes = [];
                    this.clearCanvas();
                    this.currentNode.name = '';
                    this.currentNode.modelType = '';
                    this.currentNode.version = '';
                    this.currentNode.authorName = '';
                    this.currentNode.toolkitType = '';
                    this.currentNode.company = '';
                    this.currentNode.uuid = '';
                },
                    () => {
                        this.snackBarService.error('清空组合方案失败!');
                    });
            }
        });
        this.newCompositionButton = false;
        this.clearCompositeSolutionButton = true;
        this.closeCompositeSolutionButton = false;
    }
    closeCompositeSolution() {
        this.confirmService.ask('确定要关闭该组合方案？').then((confirm) => {
            if (confirm) {
                this.compositeSolutionService.closeCompositeSolution(this.currentUser.id, this.currentSolution.uuid).subscribe(() => {
                        this.snackBarService.success('关闭组合方案成功!');
                        const index =  this.selected.value;
                        this.tabs.splice(index, 1);
                        this.solutions.splice(index, 1);
                        this.currentNode.name = '';
                        this.currentNode.modelType = '';
                        this.currentNode.version = '';
                        this.currentNode.authorName = '';
                        this.currentNode.toolkitType = '';
                        this.currentNode.company = '';
                        this.currentNode.uuid = '';
                    },
                    () => {
                        this.snackBarService.error('关闭组合方案失败!');
                    });
            }
        });
        this.newCompositionButton = false;
        this.clearCompositeSolutionButton = false;
        this.closeCompositeSolutionButton = true;
    }
    // editeNode(node) {
    //     const dialogRef = this.dialog.open(OchestratorEditeNodeComponent, {
    //         width: '450px',
    //         data: {
    //             node: {node}
    //         },
    //     });
    //     dialogRef.afterClosed().subscribe((s) => {
    //         if (!s) {
    //             return;
    //         }
    //         node.name = s;
    //     });
    // }

    getNodeSolutionIdByNodeId(nodeId: string): string {
        let sid = null;
        this.currentSolution.nodes.forEach((node: MyNode) => {
            if (node.id === nodeId) {
                sid = node.nodeSolutionId;
            }
        });
        return sid;
    }
    addLinkToRemote(path: MyPath): Observable<HttpResponse<any>> {
        const from = this.inputPortsMap.get(path.from);
        const to = this.outputPortsMap.get(path.to);
        const data: any = {};
        data.userId = this.currentUser.login;
        data.solutionId = this.currentSolution.uuid;
        data.linkName = path.name;

        data.sourceNodeName = path.fromName;
        data.sourceNodeId = path.from;
        data.targetNodeName = path.toName;
        data.targetNodeId = path.to;
        if (from) {
            data.sourceNodeRequirement = from[0].portName;
            const ssId = this.getNodeSolutionIdByNodeId(path.from);
            if (ssId != null) {
                const method = this.modelMethodMap.get(ssId);
                if (method != null) {
                    data.sourceNodeRequirement = method;
                }
            }
        }else {
            data.sourceNodeRequirement = '';
        }
        if (to) {
            data.targetNodeCapabilityName = to[0].portName;
            const tsId = this.getNodeSolutionIdByNodeId(path.to);
            if (tsId != null) {
                const method = this.modelMethodMap.get(tsId);
                if (method != null) {
                    data.targetNodeCapabilityName = method;
                }
            }
        }else {
            data.targetNodeCapabilityName = '';
        }
        data.input = path.input;
        data.output = path.output;
        data.end = path.end;
        data.start = path.start;
        data.linkId = path.id;
        return this.ochestratorService.createLink(data);

    }
    setSolutionModelMethod(solutionId) {
        if (this.modelMethodMap.get(solutionId) == null) {
            this.documentService.query({
                solutionUuid: solutionId,
                name: 'api-example.txt',
            }).subscribe(
                (res) => {
                    if (res && res.body.length > 0) {
                        const url = res.body[0].url;
                        this.downloadService.getFileText(url).subscribe(
                            (res1) => {
                                this.modelMethodMap.set(solutionId, JSON.parse(res1.body['text'])['examples'][0]['model-method']);
                            }
                        );
                    }
                }
            );
        }
    }

    editeLink(path) {
        const dialogRef = this.dialog.open(OchestratorEditeLinkComponent, {
            width: '450px',
            data: {
                userId: this.currentUser.id,
                solutionId: this.currentSolution.uuid,
                linkId: path.id,
                linkName: path.name
            },
        });
        dialogRef.afterClosed().subscribe((s) => {
            if (!s) {
                return;
            }
            // selectAll()
            select('#pathText' + path.id).selectAll('textPath').text(s);
            path.name = s;
        });
    }
    // deleteNodeToRemote() {
    //
    // }

    clickLink(d) {

    }

}
