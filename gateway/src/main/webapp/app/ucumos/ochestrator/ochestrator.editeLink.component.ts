import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import {SnackBarService} from '../../shared';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CompositeSolutionService} from '../service/compositeSolution.service';
import {OchestratorService} from '../service/ochestrator.service';

@Component({
    templateUrl: './ochestrator.editeLink.component.html'
})
export class OchestratorEditeLinkComponent implements OnInit {

    edite: boolean;
    header: string;
    formGroup: FormGroup;
    get name() { return this.formGroup.get('name'); }

    constructor(
        private formBuilder: FormBuilder,
        public dialogRef: MatDialogRef<OchestratorEditeLinkComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private snackBarService: SnackBarService,
        private ochestratorService: OchestratorService,
    ) {
        this.formGroup = this.formBuilder.group({
            name: ['', {
                validators: [Validators.required, Validators.maxLength(50),
                    Validators.pattern('^[_A-Za-z0-9-]*$')],
                updateOn: 'blur',
            }],
         });
    }

    ngOnInit() {
        this.edite = false;
        this.header = '链路名称';
        if (this.data.linkName) {
            this.edite = true;
            this.header = '修改链路名称';
            this.name.setValue(this.data.linkName);
        }

    }

    onClose(): void {
        this.dialogRef.close(null);
    }

    onSubmit() {
        if (this.edite) {
            const data: any = {};
            data.userId = this.data.login;
            data.solutionId = this.data.solutionId;
            data.linkId = this.data.linkId;
            data.linkName = this.name.value;
            this.ochestratorService.updateLink(data).subscribe((res) => {
                    this.dialogRef.close(data.linkName);
                },
                (err) => {
                    this.snackBarService.error('修改链路名称失败');
                });
        } else {
            this.dialogRef.close(this.name.value);
        }

        // this.dialogRef.close(this.sol);
        // this.solutionSharedService.create(solutionShared).subscribe(() => {
        //     this.snackBarService.success('解决方案' + this.formGroup.solution.value + '创建成功！');
        //     this.onClose();
        // }, () => {
        //     this.snackBarService.error('模型分享成功！');
        // });
    }

}
