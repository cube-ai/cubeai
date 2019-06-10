import {Component, OnInit} from '@angular/core';
import {ConfirmService, SnackBarService} from '../../shared';
import {Principal, User} from '../../account';
import {SolutionService} from '../service/solution.service';
import {FormControl} from '@angular/forms';

import {select, selectAll, event} from 'd3-selection';
import {drag} from 'd3-drag';
// import d3 = require('d3')
import { toInt} from 'ngx-bootstrap/chronos/utils/type-checks';
import { toInteger} from '@ng-bootstrap/ng-bootstrap/util/util';
import { Observable} from 'rxjs/Rx';
import { map, startWith} from 'rxjs/operators';
import { MatDialog} from '@angular/material';
import { AddSolutionComponent} from './ochestrator.addSolution.component';

class MyCircle {
    id: any;
    type: any;
    number: any;
    parentId: any;
}
class MyNode {
    id: any;
    dataId: any;
    x: any;
    y: any;
    text: any;
    inputs: any;
    outputs: any;
    inputCircles: MyCircle[];
    outputCircles: MyCircle[];
}

class MyPath {
    id: any;
    from: any;
    start: any;
    d: any;
    to: any;
    input: any;
    output: any;
    end: any;
}
class MySolution {
    workflow: any;
    // expand = true;
    activeLine = null;
    points = [];
    translate = null;
    drawLine = false;
    dx = 0;
    dy = 0;
    dragElem = null;
    nodes = [];
    gNodes: any;
    paths: MyPath[];
    curPath: MyPath;
}
@Component({
    templateUrl: './ochestrator.component.html',
    styleUrls: [
        // '../ucumos-datapage.css',
        'ochestrator.css',
    ],
})
export class OchestratorComponent implements OnInit {

    currentUser: User;
    userLogin: string;
    categoryNames: any[];
    checkboxDisable: boolean;
    activeInactivedeploy: boolean;
    expanded: boolean;
    cats: any[];
    tabs = ['解决方案 1'];
    selected = new FormControl(0);

    canvas: boolean;
    serchControl =  new FormControl();
    filteredItems: Observable<any[]>;

    solutions: MySolution[];
    currentSolution: MySolution;
    // workflow: any;
    // // expand = true;
    // activeLine = null;
    // points = [];
    // translate = null;
    // drawLine = false;
    // dx = 0;
    // dy = 0;
    // dragElem = null;
    // nodes = [];
    // gNodes: any;
    constructor(
        private principal: Principal,
        private confirmService: ConfirmService,
        private snackBarService: SnackBarService,
        private solutionService: SolutionService,
        private dialog: MatDialog,
    ) {
        this.filteredItems = this.serchControl.valueChanges
            .pipe(
                startWith(''),
                map((state) => state ? this._filterStates(state) : this.categoryNames.slice())
            );
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

    ngOnInit() {
        this.currentUser = this.principal.getCurrentAccount() ? this.principal.getCurrentAccount() : null;
        this.cats = ['1', '2', '3'];
        this.expanded = false;
        this.userLogin = this.principal.getCurrentAccount().login;
        this.categoryNames = [{ name: 'Classification', expand: false, nodes: [{name: 'Classifier', id: 'id101'}, {name: 'Classifier-DM', id: 'id102'}]},
            { name: 'Prediction', expand: false, nodes: [{name: 'Predictor', id: 'id211'}, {name: 'Predictor-DM', id: 'id212'}]},
            { name: 'Regression', expand: false, nodes: []},
            { name: 'Other', expand: false, nodes: [] }];
        this.checkboxDisable = false;
        this.activeInactivedeploy = false;
        this.canvas = true;

        this.expanded = false;
        this.solutions = [];
        this.currentSolution = new MySolution();
        this.currentSolution.workflow = {
        };
        this.currentSolution.workflow.nodes = new MyNode();
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
            this.addSolution(s.name, selectAfterAdding);
        });
    }
    addSolution(name: any, selectAfterAdding: boolean) {
        this.tabs.push(name);
        this.currentSolution = new MySolution();
        this.currentSolution.workflow = {
        };
        this.currentSolution.workflow.nodes = new MyNode();
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
    }

    removeTab(index: number) {
        if (this.tabs.length === 1) {
            this.snackBarService.error('唯一的解决方案不能删除');
            return;
        }
        this.tabs.splice(index, 1);
        this.solutions.splice(index, 1);
        // if (index == this.selected.value) {
        //     if(th)
        // }
        // this.currentSolution = this.solutions[this.selected.value];
    }

    changeSolution(e) {
        this.currentSolution = this.solutions[e];
    }

    updateResource() {
        this.currentSolution = this.solutions[this.selected.value];
        select('svg').selectAll('path.cable').data([]).exit().remove();
        select('svg').selectAll('g').data([]).exit().remove();
        this.updateNode(this.currentSolution.nodes);
        this.updatePath(this.currentSolution.paths);
        this.currentSolution.nodes.forEach((node: MyNode) => {
            this.updateCable(select('#' + node.id));
        });
        // this.updatePath(this.currentSolution.paths);
    }

    updatePath(paths: MyPath[]) {
        // const t =  select('svg').selectAll('path.cable').data([]);
        // const t2 =  select('svg').selectAll('path .cable').data([]);
        select('svg').selectAll('path.cable').data(paths).enter()
            .append('path')
            .attr('class', 'cable')
            .attr('stroke', '#333')
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
            .attr('end', (path) => path.end);
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
    }

    save() {
    }
    newSolution(ar) {

    }
    setProbe(a) {

    }
    showDeleteNodeLink() {

    }
    clearSolution() {

    }

    dragElStart(e) {
        e.dataTransfer.setData('Text', e.target.id);
    }

    linestarted(d) {
        const id: string = '#' + d.id.toString();
        this.currentSolution.drawLine = false;
        const e: any = id;
        // 当前选中的circle
        const anchor = select(e);
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
        this.currentSolution.curPath.start = dx + ', ' + dy;
        // this.currentSolution.curPath.start.dx = dx;
        // this.currentSolution.curPath.start.dy = dy;
        this.currentSolution.curPath.output = select(e).attr('output');
        this.currentSolution.paths.push(this.currentSolution.curPath);
        this.currentSolution.activeLine = select('svg').selectAll('path.cable').data(this.currentSolution.paths).enter()
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

    lineended(d: any) {
        this.currentSolution.drawLine = false;
        const anchor = selectAll('circle.end');
        if (anchor.empty()) {
            this.currentSolution.activeLine.remove();
            this.currentSolution.paths.pop();
            select('svg').selectAll('path.cable').data(this.currentSolution.paths).exit().remove();
        } else {
            const pNode = select((anchor.node() as any).parentNode);
            const input = toInt(anchor.attr('input')) * (pNode.node() as any).getBoundingClientRect().width / (toInt(pNode.attr('inputs')) + 1);
            anchor.classed('end', false);

            this.currentSolution.curPath.to = pNode.attr('id');
            this.currentSolution.curPath.input = anchor.attr('input');
            this.currentSolution.curPath.end = input + ', 0';

            // this.currentSolution.activeLine.attr('to', pNode.attr('id'));
            // this.currentSolution.activeLine.attr('input', anchor.attr('input'));
            // this.currentSolution.activeLine.attr('end', input + ', 0');
            this.currentSolution.activeLine.attr('to', (path) => path.to);
            this.currentSolution.activeLine.attr('input', (path) => path.input);
            this.currentSolution.activeLine.attr('end', (path) => path.end);
            this.currentSolution.activeLine.attr('cursor', 'pointer');
            this.currentSolution.activeLine.on('dblclick', (path) => { this.removePath(path); });
            // this.currentSolution.curPath.d = this.currentSolution.activeLine.node().attr('d');
            // this.currentSolution.paths.push(this.currentSolution.curPath);
        }
        this.currentSolution.curPath = new MyPath();
        this.currentSolution.activeLine = null;
        this.currentSolution.points.length = 0;
        this.currentSolution.translate = null;
    }
    removePath(path: MyPath) {
        const index = this.currentSolution.paths.findIndex((e) => {
            // return e.from === path.from && e.to === path.to;
            return e.id === path.id;
        });
        this.currentSolution.paths.splice(index, 1);
        select('#' + path.id).remove();
    }

    getTranslate(transform) {
        const arr = transform.substring(transform.indexOf('(') + 1, transform.indexOf(')')).split(',');
        return [+arr[0], +arr[1]];
    }

    dragstarted(e) {
        const id: string = '#' + e.id.toString();
        this.currentSolution.dragElem = select(id);
        const transform = this.currentSolution.dragElem .attr('transform');
        const translate = this.getTranslate(transform);
        this.currentSolution.dx = event.x - translate[0];
        this.currentSolution.dy = event.y - translate[1];
        this.currentSolution.dragElem = select(id);
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
        const bound = elem.node().getBoundingClientRect();
        const width = bound.width;
        const height = bound.height;
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
            const end: any = path.substring(path.lastIndexOf(' ') + 1).split(',');
            end[0] = toInt(end[0]);
            end[1] = toInt(end[1]);

            select(this).attr('d', function() {
                return 'M' + start[0] + ',' + start[1]
                    + ' C' + start[0] + ',' + (start[1] + end[1]) / 2
                    + ' ' + end[0] + ',' +  (start[1] + end[1]) / 2
                    + ' ' + end[0] + ',' + end[1];
            });
        });

        // 更新输入线的位置
        const toVar = selectAll(fromTo);
        toVar.each(function(d) {
            const path = select(this).attr('d');
            const start: any = path.substring(1, path.indexOf('C')).split(',');
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
        });

    }

    dragended(e) {
        this.currentSolution.dx = this.currentSolution.dy = 0;
        this.currentSolution.dragElem = null;
        // $('moveNode').removeClass('moveNode');
    }
    initSVGElement() {
        this.currentSolution.nodes = [];
        this.currentSolution.gNodes = selectAll('g').data(this.currentSolution.nodes);
    }

    updateNode(nodes: MyNode[]) {
        const svg = select('svg');
        this.currentSolution.nodes = [];
        // svg.selectAll('g').data(this.currentSolution.nodes).exit().remove();
        nodes.forEach((node: MyNode) => {
            this.addNode(svg, node);
        });
    }
    addNode(svg, node: MyNode) {
        this.currentSolution.nodes.push(node);
        const gNodes = svg.selectAll('g').data(this.currentSolution.nodes);
        // 计算节点编号
        const g = gNodes.enter().append('g')
            .attr('class', 'node')
            .attr('data-id', function(d) {return d.dataId; })
            .attr('id', function(d) {
                return d.id;
            })
            // .attr('x', function(d) {
            //     return d.x;
            // })
            // .attr('y', function(d) {
            //     return d.y;
            // });
            .attr('transform', function(d) {
                return 'translate(' + d.x + ', ' + d.y + ')';
            });
// 计算节点编号
// let g = svg.append('g')
//     .attr('class', 'node')
//     .attr('data-id', node.dataId)
//     .attr('id', node.id)
//     .attr('transform', 'translate(' + node.x + ', ' + node.y + ')');

        const rect = g.append('rect')
            .attr('rx', 5)
            .attr('ry', 5)
            .attr('stroke-width', 2)
            .attr('stroke', '#333')
            .attr('fill', '#fff')
            .attr('width', '180px')
            .attr('height', '36px')
            .attr('cursor', 'pointer');

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
        const inputs = node.inputs || 0;
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
            .attr('r', 5)
            .attr('stroke-width', '2px')
            .attr('cursor', 'pointer')
            .attr('fill', '#fff');
        // for (let i = 0; i < inputs; i++) {
        //     g.append('circle')
        //         .attr('class', 'input')
        //         .attr('input', (i + 1))
        //         .attr('cx', width * (i + 1) / (inputs + 1))
        //         .attr('cy', 0)
        //         .attr('r', 5)
        //         .attr('stroke-width','2px')
        //         .attr('cursor','pointer')
        //         .attr('fill','#fff');
        // }

        // output circle
        const outputs = node.outputs || 0;
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
            .attr('r', 5)
            .attr('stroke-width', '2px')
            .attr('cursor', 'pointer');
        // for (let i = 0; i < outputs; i++) {
        //     g.append('circle')
        //         .attr('output', (i + 1))
        //         .attr('class', 'output')
        //         .attr('cx', width * (i + 1) / (outputs + 1))
        //         .attr('cy', height)
        //         .attr('r', 5)
        //         .attr('stroke-width','2px')
        //         .attr('cursor','pointer');
        // }

        g.call(
            drag()
                .on('start', (d) => {this.dragstarted(d); })
                .on('drag', (d) => {this.dragged(d); })
                .on('end', (d) => {this.dragended(d); })
        );
        g.selectAll('rect').on('dblclick', (d) => {
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
            // 更新输出线的位置
        });

        g.selectAll('circle.output').call(
            drag()
                .on('start', (d) => { this.linestarted(d); })
                .on('drag', (d) => { this.linedragged(); })
                .on('end', (d) => { this.lineended(d); })
        );
        const drawLine = this.currentSolution.drawLine;
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
        const dataId = e.dataTransfer.getData('Text');

        // 防止对线段的拖拽
        if (!dataId.startsWith('id')) {
            return;
        }
        const el1 = select('#idsw-bpmn').node()  as HTMLElement;
        const b = el1.getBoundingClientRect();
        const el = <HTMLElement>document.getElementById('' + dataId).lastChild;
        const dx: any = e.clientX - b.left;
        // x = toInteger(x.replace('px','')) - 250;
        const dy: any = e.clientY - b.top;
        // y = toInteger(y.replace('px','')) - 40;
        const node: MyNode = {
            id: 'id' + new Date().getTime(),
            dataId: toInteger(dataId),
            x: dx,
            y: dy,
            text: el.textContent,
            inputs: 2,
            outputs: 2,
            inputCircles: [],
            outputCircles: [],
        };
        // if(node.dataId == 101) {
        //     node.inputs = 0;
        //     node.outputs = 1;
        // } else if(node.dataId == 102) {
        //     node.inputs = 1;
        //     node.outputs = 0;
        // } else {
        //     node.inputs = 1;
        //     node.outputs = 1;
        // }

        // 计算节点编号
        if (this.currentSolution.workflow.nodes[node.dataId]) {
            this.currentSolution.workflow.nodes[node.dataId] += 1;
        }  else {
            this.currentSolution.workflow.nodes[node.dataId] = 1;
        }

        for (let i = 0; i < node.inputs; i++) {
            const c: MyCircle = {
                id: 'idcin' + i.toString() + new Date().getTime(),
                type: 0,
                number: i + 1,
                parentId: node.id
            };
            node.inputCircles.push(c);
        }
        for (let i = 0; i < node.outputs; i++) {
            const c: MyCircle = {
                id: 'idcout' + i.toString() + new Date().getTime(),
                type: 1,
                number: i + 1,
                parentId: node.id
            };
            node.outputCircles.push(c);
        }
        const svg = select('svg');
        this.addNode(svg, node);
    }
    dragElenter(e) {
    }
    dragElover(e) {
        e.preventDefault();
    }
    dragElend(e) {
        e.preventDefault();
    }
}
