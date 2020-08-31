import {Component, OnInit, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { FormControl, Validators } from '@angular/forms';
import {SnackBarService, User, HttpService} from '../../shared';

@Component({
    templateUrl: './user-password.component.html'
})
export class UserPasswordComponent implements OnInit {
    password: FormControl;
    user: User;

    constructor(public dialogRef: MatDialogRef<UserPasswordComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any,
                private http: HttpService,
                private snackBarService: SnackBarService,
    ) {
    }

    ngOnInit() {
        this.password = new FormControl('', [Validators.required, Validators.maxLength(50), Validators.minLength(4)]);
        const body = {
            action: 'find_user',
            args: {
                login: this.data.login,
            }
        };
        this.http.post('uaa', body).subscribe((res) => {
            if (res.body['status'] === 'ok') {
                this.user = res.body['value'];
            }
        });
    }

    onClose(): void {
        this.dialogRef.close();
    }

    onSubmit() {
        this.user.password = this.password.value;
        const body = {
            action: 'update_user',
            args: {
                user: this.user,
            }
        };
        this.http.post('uaa', body).subscribe((res) => {
            if (res.body['status'] === 'ok') {
                this.snackBarService.success('用户密码修改成功！');
                this.onClose();
            } else {
                this.snackBarService.error('用户密码修改失败！');
            }
        }, () => {
            this.snackBarService.error('用户密码修改失败！');
        });
    }

}
