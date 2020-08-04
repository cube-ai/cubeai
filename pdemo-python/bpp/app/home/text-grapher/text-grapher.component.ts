import {Component, OnInit} from '@angular/core';
import {VisNodes, VisEdges, VisNetworkData, VisNetworkOptions} from 'ng2-vis';

@Component({
    templateUrl: './text-grapher.component.html',
})
export class TextGrapherComponent implements OnInit {

    text: string;
    error: boolean;
    sending = false;

    public visNetwork: string;
    public visNetworkData: VisNetworkData;
    public visNetworkOptions: VisNetworkOptions;

    public ngOnInit(): void {
        this.visNetwork = 'networkTextGrapher';
        this.visNetworkData = null;
        this.visNetworkOptions = {};
    }

    predict() {
        const url = 'https://cubeai.dimpt.com/ability/model/998201a434924e988adf31c4ad2065e3/predict';

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'text': this.text,
            }),
            method: 'POST',
        };

        this.sending = true;
        this.error = false;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                const results = JSON.parse(JSON.parse(res)['value']);
                const nodes = new VisNodes(results['nodes']);
                const edges = new VisEdges(results['edges']);
                this.visNetworkData = {
                    nodes,
                    edges,
                };
                this.sending = false;
            })
            .catch((error) => {
                this.error = true;
                this.sending = false;
                this.visNetworkData = null;
            });
    }

}
