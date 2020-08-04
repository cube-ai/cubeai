import { Component, OnInit } from '@angular/core';
import { GatewayRoutesService } from './gateway-routes.service';
import { GatewayRoute } from './gateway-route.model';
import {GlobalService} from '../../shared';

@Component({
    selector: 'jhi-gateway',
    templateUrl: './gateway.component.html',
    styleUrls: [
        '../admin-datapage.css',
    ],
    providers: [ GatewayRoutesService ]
})
export class JhiGatewayComponent implements OnInit {

    gatewayRoutes: GatewayRoute[];
    updatingRoutes: Boolean;

    constructor(
        public globalService: GlobalService,
        private gatewayRoutesService: GatewayRoutesService
    ) {
    }

    ngOnInit() {
        if (window.screen.width < 960) {
            this.globalService.closeSideNav(); // 手机屏幕默认隐藏sideNav
        }

        this.refresh();
    }

    refresh() {
        this.updatingRoutes = true;
        this.gatewayRoutesService.findAll().subscribe((routes) => {
            this.gatewayRoutes = routes;
            this.updatingRoutes = false;
        });
    }
}
