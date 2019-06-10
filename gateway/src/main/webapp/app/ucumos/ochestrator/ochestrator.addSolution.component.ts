import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import {SnackBarService} from '../../shared';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
    templateUrl: './ochestrator.addSolution.component.html'
})
export class AddSolutionComponent implements OnInit {
    formGroup: FormGroup;
    sol: any;
    get solution() { return this.formGroup.get('solution'); }

    constructor(
        private formBuilder: FormBuilder,
        public dialogRef: MatDialogRef<AddSolutionComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private snackBarService: SnackBarService,
    ) {
        this.formGroup = this.formBuilder.group({
            solution: ['', {
                validators: [Validators.required, Validators.maxLength(50),
                    Validators.pattern('^[_.@A-Za-z0-9-]*$')],
                updateOn: 'blur',
            }],
         });
    }

    ngOnInit() {
        this.sol = {};
    }

    onClose(): void {
        this.dialogRef.close(null);
    }

    onSubmit() {
        this.sol.name = this.solution.value;
        this.sol.username = this.data.fromUserLogin;
        this.dialogRef.close(this.sol);
        // this.solutionSharedService.create(solutionShared).subscribe(() => {
        //     this.snackBarService.success('解决方案' + this.formGroup.solution.value + '创建成功！');
        //     this.onClose();
        // }, () => {
        //     this.snackBarService.error('模型分享成功！');
        // });
    }

}
