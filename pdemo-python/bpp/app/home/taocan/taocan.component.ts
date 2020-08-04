import {Component, OnInit} from '@angular/core';

@Component({
    templateUrl: './taocan.component.html',
})
export class TaocanComponent implements OnInit {

    taocan: string;
    sat: string;
    sending = false;
    taocanFields = '省份中文名称, 地市中文名称, 用户ID_NEW, 用户满意度, 提及A网络标签, 提及B语音标签, 提及B上网标签, 提及C语音信号覆盖标签, ' +
        '提及C语音信号稳定性标签, 提及C上网信号覆盖标签, 提及C上网信号稳定性标签, 提及C上网速度标签, 提及B室内信号标签, 提及B室外信号标签, ' +
        '业务类型, VIP级别, 付费模式, 产品类别, 是否在网, 是否上网卡, 客户年龄, 客户性别, DOU, 非漫游的总流量数, MOU, ' +
        '主叫的总分钟数, 被叫的总分钟数, 非漫游的总分钟数, 月度月小区总数, ARPU, 套餐费用, 超套月流量费用, 超套月语音费用, 超套月短信费用, ' +
        '超套月增值费用, 是否合约计划, 融合类型, 终端类型, 终端厂家, 终端型号, 是否锁频, 老人机标签, 互联网套餐, to_合约失效期, ' +
        '沉默用户, 上网用户, refer_label, unroam_dou, ori_mou, ter_mou, unroam_mou, 网龄年限';

    ngOnInit() {
    }

    predict() {
        // const url = location.protocol + '//' + location.host + '/ability/model/e38259ee57484abc8843a014ffec4efc/predict'; // 用于生产环境正式部署
        const url = 'https://cubeai.dimpt.com/ability/model/e38259ee57484abc8843a014ffec4efc/predict'; // 用于开发环境连接生产环境测试

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'taocan': this.taocan,
            }),
            method: 'POST',
        };

        this.sending = true;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                const result = JSON.parse(res)['value'];
                if (result === '1') {
                    this.sat = '满意';
                } else if (result === '0') {
                    this.sat = '不满意';
                } else {
                    this.sat = '输入参数有误，请检查后重新输入...';
                }
                this.sending = false;
            })
            .catch((error) => {
                this.sat = '出错啦...';
                this.sending = false;
            });
    }

    genTaocanExample() {
        // const url = location.protocol + '//' + location.host + '/ability/model/e38259ee57484abc8843a014ffec4efc/gen_taocan_example'; // 用于生产环境正式部署
        const url = 'https://cubeai.dimpt.com/ability/model/e38259ee57484abc8843a014ffec4efc/gen_taocan_example'; // 用于开发环境连接生产环境测试

        const params = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({
                'text': '',
            }),
            method: 'POST',
        };

        this.sending = true;
        fetch(url, params).then((data) => data.text())
            .then((res) => {
                this.taocan = JSON.parse(res)['value'];
                this.sending = false;
            })
            .catch((error) => {
                this.taocan = '出错啦...';
                this.sending = false;
            });
    }

}
