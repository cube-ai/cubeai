import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import {SnackBarService} from '../../shared';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CompositeSolutionService} from '../service/compositeSolution.service';
import {OchestratorService} from '../service/ochestrator.service';

@Component({
    templateUrl: './ochestrator.editeNode.component.html'
})
export class OchestratorEditeNodeComponent implements OnInit {
    formGroup: FormGroup;
    get name() { return this.formGroup.get('name'); }

    constructor(
        private formBuilder: FormBuilder,
        public dialogRef: MatDialogRef<OchestratorEditeNodeComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private snackBarService: SnackBarService,
        private ochestratorService: OchestratorService,
    ) {
        this.formGroup = this.formBuilder.group({
            name: ['', {
                validators: [Validators.required, Validators.maxLength(50),
                    Validators.pattern('^[_.@A-Za-z0-9-]*$')],
                updateOn: 'blur',
            }],
        });
    }

    ngOnInit() {
        this.name.setValue(this.data.node.name);
    }

    onClose(): void {
        this.dialogRef.close(null);
    }

    onSubmit() {
        // const data: any = {};
        // data.userId = this.data.userId;
        // data.solutionId = this.data.solutionId;
        // data.linkId = this.data.linkId;
        // data.linkName = this.name.value;
        // this.ochestratorService.updateLink(data).subscribe((res) => {
        //         this.dialogRef.close(this.data.linkName);
        //     },
        //     (err) => {
        //         this.snackBarService.error('修改节点名称失败');
        //     });
    }

}
