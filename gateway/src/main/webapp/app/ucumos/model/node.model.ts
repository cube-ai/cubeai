import {MyCircle, MyPort} from './circle.model';

export class MyNode {
    public id: any;
    public nodeSolutionId: any;
    public x: any;
    public y: any;
    public text: any;
    // public inputs: any;
    // public outputs: any;
    public inputCircles: MyPort[];
    public outputCircles: MyPort[];
    public type: any;
}

export class Ndata {
    public px: string;
    public py: string;
    public radius?: string;
    public fixed?: boolean;
    public type: any;
}
export class Node {
    public name: string;
    public nodeId: string;
    public nodeSolutionId: string;
    public nodeVersion: string;
    public requirements?: any;
    public properties?: any;
    public capabilities?: any;
    public typeInfo?: any;
    public ndata: Ndata;
    public type: any;
}
