import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import {SnackBarService} from '../../shared';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {NoLoginValidator} from '../../shared/form-validators';
import {SolutionSharedService} from '../';
import {SolutionShared} from '../model/solution-shared.model';

@Component({
    templateUrl: './share-solution.component.html'
})
export class ShareSolutionComponent implements OnInit {
    formGroup: FormGroup;

    get toUserLogin() { return this.formGroup.get('toUserLogin'); }

    constructor(
        private formBuilder: FormBuilder,
        public dialogRef: MatDialogRef<ShareSolutionComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private solutionSharedService: SolutionSharedService,
        private snackBarService: SnackBarService,
        private noLoginValidator: NoLoginValidator,
    ) {
        this.formGroup = this.formBuilder.group({
            toUserLogin: ['', {
                validators: [Validators.required, Validators.maxLength(50),
                    Validators.pattern('^[_.@A-Za-z0-9-]*$')],
                asyncValidators: [this.noLoginValidator.validate()],
                updateOn: 'blur',
            }],
         });
    }

    ngOnInit() {
    }

    onClose(): void {
        this.dialogRef.close();
    }

    onSubmit() {
        const solutionShared = new SolutionShared();
        solutionShared.toUserLogin = this.toUserLogin.value;
        solutionShared.solutionUuid = this.data.solution.uuid;
        solutionShared.solutionName = this.data.solution.name;
        solutionShared.solutionAuthor = this.data.solution.authorLogin;
        solutionShared.solutionCreatedDate = this.data.solution.createdDate;

        this.solutionSharedService.create(solutionShared).subscribe(() => {
            this.snackBarService.success('模型分享成功！');
            this.onClose();
        }, () => {
            this.snackBarService.error('模型分享成功！');
        });
    }

}
